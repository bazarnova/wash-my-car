package ru.baz.aisa.rest.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class ScheduleServiceRequest {
    private String userName;
    private String userPhone;
    private LocalDate date;
    private List<Long> serviceIds;
    private Integer startSlot;
}
