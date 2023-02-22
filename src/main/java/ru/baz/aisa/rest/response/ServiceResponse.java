package ru.baz.aisa.rest.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceResponse {
    @ApiModelProperty(notes = "Service id", example = "1", required = true)
    private Long id;

    @ApiModelProperty(notes = "Service name.", example = "Car polishing.", required = true)
    private String name;

    @ApiModelProperty(notes = "Number of slots required to this service.", example = "2", required = true)
    private Integer slots;

    @ApiModelProperty(notes = "Needed time for this service. Format: HH:mm.", example = "00:15", required = true)
    private String time;
}
