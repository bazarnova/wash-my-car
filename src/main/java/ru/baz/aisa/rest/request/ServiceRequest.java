package ru.baz.aisa.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceRequest {
    private String name;
    private Integer slots;
}
