package ru.baz.aisa.rest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SlotStepResponse {
    private Integer id;
    private String time;
    private Long serviceId;
}
