package ru.baz.aisa.rest.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SlotResponse {
    @ApiModelProperty(notes = "Service entry date.", example = "2023-02-22", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ApiModelProperty(notes = "List of slots.", example = "[\n" +
            "        {\n" +
            "            \"id\": 20,\n" +
            "            \"time\": \"05:00\",\n" +
            "            \"serviceId\": 1\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 21,\n" +
            "            \"time\": \"05:15\",\n" +
            "            \"serviceId\": 2\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 22,\n" +
            "            \"time\": \"05:30\",\n" +
            "            \"serviceId\": 2\n" +
            "        }\n" +
            "    ]", required = true)
    private List<SlotStepResponse> timeSlots;
}

