package ru.baz.aisa.rest.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SlotStepResponse {
    @ApiModelProperty(notes = "SlotStep id. SlotSteps are some predetermined time intervals.", example = "1", required = true)
    private Integer id;

    @ApiModelProperty(notes = "Needed time for this service. Format: HH:mm.", example = "00:15", required = true)
    private String time;

    @ApiModelProperty(notes = "Service id.", example = "1", required = true)
    private Long serviceId;
}
