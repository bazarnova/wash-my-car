package ru.baz.aisa.rest.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceResponse {
    private Long id;
    private String name;
    private Integer slots;
    private String time;
}
