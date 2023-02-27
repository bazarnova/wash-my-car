package ru.baz.aisa.rest.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.baz.aisa.rest.request.ScheduleServiceRequest;
import ru.baz.aisa.rest.response.SlotStepResponse;
import ru.baz.aisa.service.ScheduleService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ScheduleController {
    private final ScheduleService scheduleService;

    @ApiOperation(value = "Add new slots in schedule", notes = "Returns created slots")
    @PutMapping("/schedule")
    public ResponseEntity<Map<LocalDate, List<SlotStepResponse>>> save(@RequestBody ScheduleServiceRequest request) {
        Map<LocalDate, List<SlotStepResponse>> response = scheduleService.addNewSlot(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Get available slots in the specified period", notes = "Returns empty slots")
    @GetMapping("/schedule")
    public ResponseEntity<Map<LocalDate, List<SlotStepResponse>>> getAvailableSlots(@RequestParam("days") Integer days) {
        Map<LocalDate, List<SlotStepResponse>> responses = scheduleService.getSlotsForPeriod(days);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @ApiOperation(value = "Get reserved slots for the user", notes = "Returns reserved slots")
    @GetMapping("/schedule/user/{id}")
    public ResponseEntity<Map<LocalDate, List<SlotStepResponse>>> getSlotsForUser(@PathVariable Long id) {
        Map<LocalDate, List<SlotStepResponse>> responses = scheduleService.getSlotsForUser(id);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
