package ru.baz.aisa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.baz.aisa.entity.Schedule;

import java.time.LocalDate;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> getAllByUserIdAndDateIsGreaterThanEqual(Long id, LocalDate date);

    @Query(value = "from Schedule as s where s.date between :today and :offset")
    List<Schedule> getScheduleForPeriod(LocalDate today, LocalDate offset);

    List<Schedule> getSchedulesByDate(LocalDate date);
}
