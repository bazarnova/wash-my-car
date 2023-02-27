package ru.baz.aisa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.baz.aisa.entity.Schedule;
import ru.baz.aisa.entity.Service;
import ru.baz.aisa.entity.User;
import ru.baz.aisa.exception.ApplicationException;
import ru.baz.aisa.repository.ScheduleRepository;
import ru.baz.aisa.rest.request.ScheduleServiceRequest;
import ru.baz.aisa.rest.response.SlotStepResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final SlotHelperService slotHelperService;
    private final ServiceCatalogService serviceCatalogService;
    private final ConcurrentHashMap<LocalDate, Object> locks = new ConcurrentHashMap<>();


    @Transactional
    public Map<LocalDate, List<SlotStepResponse>> getSlotsForPeriod(Integer days) {
        Map<LocalDate, List<SlotStepResponse>> result = new HashMap<>();
        List<Schedule> responses = scheduleRepository.getScheduleForPeriod(LocalDate.now(), LocalDate.now().plusDays(days));
        Map<LocalDate, List<Schedule>> map = responses.stream().collect(Collectors.groupingBy(Schedule::getDate));

        for (int dayOffset = 0; dayOffset < days; dayOffset++) {
            LocalDate localDate = LocalDate.now();
            LocalDate offset = localDate.plusDays(dayOffset);

            Predicate<Integer> slotIsNotBusy = integer -> map.get(offset) == null || (map.get(offset) != null && map.get(offset).stream().noneMatch(s -> Arrays.asList(s.getSlots()).contains(integer)));
            List<SlotStepResponse> list = slotHelperService.getSlotsForAllDay().stream()
                    .filter(slotIsNotBusy)
                    .map(integer -> new SlotStepResponse(integer, slotHelperService.formattedSlot(integer, offset), null))
                    .collect(Collectors.toList());

            result.put(offset, list);
        }

        return result;
    }

    @Transactional
    public Map<LocalDate, List<SlotStepResponse>> getSlotsForUser(Long id) {
        Map<LocalDate, List<SlotStepResponse>> result = new HashMap<>();
        List<Schedule> list = scheduleRepository.getAllByUserIdAndDateIsGreaterThanEqual(id, LocalDate.now());

        Map<LocalDate, List<Schedule>> map = list.stream().collect(Collectors.groupingBy(Schedule::getDate));

        for (LocalDate key : map.keySet()) {
            List<Integer> slotsForDay = map.get(key).stream()
                    .flatMap(schedule -> Arrays.stream(schedule.getSlots()))
                    .collect(Collectors.toList());

            List<SlotStepResponse> stepResponses = slotsForDay.stream()
                    .map(integer -> {
                        Schedule scheduleWithSlotId = map.get(key).stream().filter(schedule -> Arrays.asList(schedule.getSlots()).contains(integer)).findFirst().orElse(null);
                        Long serviceId = scheduleWithSlotId == null ? null : scheduleWithSlotId.getServiceId();
                        return new SlotStepResponse(integer, slotHelperService.formattedSlot(integer, key), serviceId);
                    })
                    .collect(Collectors.toList());
            result.put(key, stepResponses);
        }
        return result;
    }

    @Transactional
    public Map<LocalDate, List<SlotStepResponse>> addNewSlot(ScheduleServiceRequest request) {
        Object lock = locks.computeIfAbsent(request.getDate(), k -> new Object());

        Map<LocalDate, List<SlotStepResponse>> response = new HashMap<>();
        synchronized (lock) {

            User user = userService.getOrCreateUser(request.getUserName(), request.getUserPhone());
            List<Service> serviceFromRequest = serviceCatalogService.getServices(request.getServiceIds());

            int totalSlotsNeeded = serviceFromRequest.stream().map(Service::getSlots).mapToInt(Integer::intValue).sum();
            List<Integer> slotsNeeded = Stream.iterate(request.getStartSlot(), i -> i + 1).limit(totalSlotsNeeded).collect(Collectors.toList());

            for (Integer integer : slotsNeeded) {
                LocalDateTime dateTimeForSlot = slotHelperService.fromSlotAndDate(integer, request.getDate());
                if (LocalDateTime.now().isAfter(dateTimeForSlot)) {
                    throw new ApplicationException("Cannot schedule in the past");
                }
            }

            Map<Long, List<Integer>> slotsByServiceId = new HashMap<>();
            int startFrom = request.getStartSlot();
            for (Service service : serviceFromRequest) {
                List<Integer> slotsNeededForService = Stream.iterate(startFrom, i -> i + 1).limit(service.getSlots()).collect(Collectors.toList());
                slotsByServiceId.put(service.getId(), slotsNeededForService);
                startFrom = startFrom + service.getSlots();
            }

            List<Schedule> scheduleListForDate = scheduleRepository.getSchedulesByDate(request.getDate());
            for (Schedule schedule : scheduleListForDate) {
                if (Arrays.stream(schedule.getSlots()).anyMatch(slotsNeeded::contains)) {
                    throw new ApplicationException("Slots are busy choose another.");
                }
            }

            List<SlotStepResponse> stepResponses = new ArrayList<>();
            for (Service service : serviceFromRequest) {
                Schedule schedule = new Schedule();
                schedule.setDate(request.getDate());
                schedule.setUserId(user.getId());
                schedule.setServiceId(service.getId());

                Integer[] slotsArr = slotsByServiceId.get(service.getId()).toArray(new Integer[0]);
                schedule.setSlots(slotsArr);

                scheduleRepository.saveAndFlush(schedule);

                slotsByServiceId.get(service.getId())
                        .forEach(integer -> stepResponses.add(new SlotStepResponse(integer, slotHelperService.formattedSlot(integer, request.getDate()), service.getId())));
            }

            response.put(request.getDate(), stepResponses);
        }

        log.info("Response = {}", response);

        return response;
    }
}
