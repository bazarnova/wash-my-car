package ru.baz.aisa.rest.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.baz.aisa.rest.request.ScheduleServiceRequest;
import ru.baz.aisa.rest.response.SlotResponse;
import ru.baz.aisa.service.ScheduleService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ScheduleController {
    private final ScheduleService scheduleService;

    @ApiOperation(value = "Add new slots in schedule", notes = "Returns created slots")
    @PutMapping("/schedule")
    public ResponseEntity<SlotResponse> save(@RequestBody ScheduleServiceRequest request) {
        SlotResponse slotResponse = scheduleService.addNewSlot(request);
        return new ResponseEntity<>(slotResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Get available slots in the specified period", notes = "Returns empty slots")
    @GetMapping("/schedule")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(@RequestParam("days") Integer days) {
        List<SlotResponse> slotResponses = scheduleService.getSlotsForPeriod(days);
        return new ResponseEntity<>(slotResponses, HttpStatus.OK);
    }

    @ApiOperation(value = "Get reserved slots for the user", notes = "Returns reserved slots")
    @GetMapping("/schedule/user/{id}")
    public ResponseEntity<List<SlotResponse>> getSlotsForUser(@PathVariable Long id) {
        List<SlotResponse> slotResponses = scheduleService.getSlotsForUser(id);
        return new ResponseEntity<>(slotResponses, HttpStatus.OK);
    }
}
