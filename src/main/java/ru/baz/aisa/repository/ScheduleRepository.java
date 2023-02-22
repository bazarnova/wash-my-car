package ru.baz.aisa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.baz.aisa.entity.Schedule;

import java.time.LocalDate;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

//    @Query(nativeQuery = true, value = "select * from {h-schema}schedule " +
//            "where date >= cast(now() as Date) " +
//            " and date <= (cast(now() as Date) + cast(cast(:days as varchar) as integer) )")
//    List<Schedule> getScheduleForPeriod( Integer days);

    List<Schedule> getAllByUserIdAndDateIsGreaterThanEqual(Long id, LocalDate date);

    @Query(value = "from Schedule as s where s.date between :today and :offset")
    List<Schedule> getScheduleForPeriod(LocalDate today, LocalDate offset);

    List<Schedule> getSchedulesByDate(LocalDate date);
}
