package ru.baz.aisa.rest.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class ScheduleServiceRequest {
    @ApiModelProperty(notes = "User name", example = "Vasia Pupkin", required = true)
    private String userName;

    @ApiModelProperty(notes = "User phone. Length must be 12 symbols", example = "891234567890", required = true)
    private String userPhone;

    @ApiModelProperty(notes = "Service entry date.", example = "2023-02-22", required = true)
    private LocalDate date;

    @ApiModelProperty(notes = "ID of selected services.", example = "[1, 2]", required = true)
    private List<Long> serviceIds;

    @ApiModelProperty(notes = "First service slot. Equivalent to the start of service entry time", example = "1", required = true)
    private Integer startSlot;
}
