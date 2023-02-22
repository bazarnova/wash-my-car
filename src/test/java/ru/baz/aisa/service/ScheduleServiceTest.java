package ru.baz.aisa.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Slf4j
class ScheduleServiceTest {

    @Test
    void test() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Duration d = Duration.ofMinutes(15);

        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        for (int i = 0; i < 96; i++) {

            Duration newDuration = d.multipliedBy(i);
            LocalDateTime dt = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0).plusMinutes(newDuration.toMinutes()));
            String formatted = DurationFormatUtils.formatDuration(newDuration.toMillis(), "H:mm", true);


            log.debug("Duration {}" ,formatted);
            log.debug("LocalDateTime {}" , dt.format(formatter));
        }


    }



}