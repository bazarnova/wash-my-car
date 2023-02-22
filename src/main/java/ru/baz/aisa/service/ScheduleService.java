package ru.baz.aisa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.baz.aisa.entity.Schedule;
import ru.baz.aisa.entity.Service;
import ru.baz.aisa.entity.User;
import ru.baz.aisa.repository.ScheduleRepository;
import ru.baz.aisa.rest.request.ScheduleServiceRequest;
import ru.baz.aisa.rest.response.SlotResponse;
import ru.baz.aisa.rest.response.SlotStepResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final SlotHelperService slotHelperService;
    private final ServiceCatalogService serviceCatalogService;


    @Transactional
    public List<SlotResponse> getSlotsForPeriod(Integer days) {
        List<SlotResponse> result = new ArrayList<>();
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
            result.add(new SlotResponse(offset, list));
        }

        return result;
    }

    @Transactional
    public List<SlotResponse> getSlotsForUser(Long id) {
        List<SlotResponse> result = new ArrayList<>();
        List<Schedule> list = scheduleRepository.getAllByUserIdAndDateIsGreaterThanEqual(id, LocalDate.now());

        Map<LocalDate, List<Schedule>> map = list.stream().collect(Collectors.groupingBy(Schedule::getDate));

        for (LocalDate key : map.keySet()) {
            List<Integer> slotsForDay = map.get(key).stream()
                    .flatMap(schedule -> Arrays.stream(schedule.getSlots()))
                    .collect(Collectors.toList());

            SlotResponse slotResponse = new SlotResponse();
            slotResponse.setDate(key);
            List<SlotStepResponse> stepResponses = slotsForDay.stream()
                    .map(integer -> {
                        Schedule scheduleWithSlotId = map.get(key).stream().filter(schedule -> Arrays.asList(schedule.getSlots()).contains(integer)).findFirst().orElse(null);
                        Long serviceId = scheduleWithSlotId == null ? null : scheduleWithSlotId.getServiceId();
                        return new SlotStepResponse(integer, slotHelperService.formattedSlot(integer, key), serviceId);
                    })
                    .collect(Collectors.toList());
            slotResponse.setTimeSlots(stepResponses);
            result.add(slotResponse);
        }
        return result;
    }

    @Transactional
    public SlotResponse addNewSlot(ScheduleServiceRequest request) {
        User user = userService.getOrCreateUser(request.getUserName(), request.getUserPhone());
        List<Service> serviceFromRequest = serviceCatalogService.getServices(request.getServiceIds());

        int totalSlotsNeeded = serviceFromRequest.stream().map(Service::getSlots).mapToInt(Integer::intValue).sum();
        List<Integer> slotsNeeded = Stream.iterate(request.getStartSlot(), i -> i + 1).limit(totalSlotsNeeded).collect(Collectors.toList());

        for (Integer integer : slotsNeeded) {
            LocalDateTime dateTimeForSlot = slotHelperService.fromSlotAndDate(integer, request.getDate());
            if (LocalDateTime.now().isAfter(dateTimeForSlot)) {
                throw new RuntimeException("Cannot schedule in the past");
            }
        }


        Map<Long, List<Integer>> slotsByServiceId = new HashMap<>();
        int startFrom = request.getStartSlot();
        for (Service service : serviceFromRequest) {
            List<Integer> slotsNeededForService = Stream.iterate(startFrom, i -> i + 1).limit(service.getSlots()).collect(Collectors.toList());
            slotsByServiceId.put(service.getId(), slotsNeededForService);
            startFrom = startFrom + service.getSlots();
        }

        //TODO lock date for update
        List<Schedule> scheduleListForDate = scheduleRepository.getSchedulesByDate(request.getDate());
        for (Schedule schedule : scheduleListForDate) {
            if (Arrays.stream(schedule.getSlots()).anyMatch(slotsNeeded::contains)) {
                throw new RuntimeException("Slots are busy choose another.");
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

        SlotResponse response = new SlotResponse();
        response.setDate(request.getDate());

        response.setTimeSlots(stepResponses);

        return response;
    }
}
