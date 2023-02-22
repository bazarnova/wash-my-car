package ru.baz.aisa.rest.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceRequest {
    @ApiModelProperty(notes = "Service name.", example = "Car polishing.", required = true)
    private String name;

    @ApiModelProperty(notes = "Number of slots required to this service.", example = "2", required = true)
    private Integer slots;
}
