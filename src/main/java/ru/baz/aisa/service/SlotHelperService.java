package ru.baz.aisa.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.stereotype.Component;
import ru.baz.aisa.config.ApplicationProperties;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SlotHelperService {

    private final ApplicationProperties applicationProperties;

    private final DateTimeFormatter slotDateTimeFormatter;

    public LocalDateTime fromSlotAndDate(Integer slotNumber, LocalDate date) {
        long minutes = applicationProperties.getSlotDuration().multipliedBy(slotNumber).toMinutes();
        int hour = applicationProperties.getDateStartHour();
        int minute = applicationProperties.getDateStartMinute();
        int second = applicationProperties.getDateStartSecond();
        return LocalDateTime.of(date, LocalTime.of(hour, minute, second).plusMinutes(minutes));
    }

    public String formattedSlot(Integer slotNumber, LocalDate date) {
        long minutes = applicationProperties.getSlotDuration().multipliedBy(slotNumber).toMinutes();
        int hour = applicationProperties.getDateStartHour();
        int minute = applicationProperties.getDateStartMinute();
        int second = applicationProperties.getDateStartSecond();
        return LocalDateTime.of(date, LocalTime.of(hour, minute, second).plusMinutes(minutes)).format(slotDateTimeFormatter);
    }

    public String formattedDuration(Integer slots) {
        Duration duration = applicationProperties.getSlotDuration().multipliedBy(slots);
        return DurationFormatUtils.formatDuration(duration.toMillis(), applicationProperties.getDateFormatSlot(), true);
    }

    public List<Integer> getSlotsForAllDay() {
        return Stream.iterate(0, i -> i + 1).limit(applicationProperties.getSlotPerDay()).collect(Collectors.toList());
    }
}
