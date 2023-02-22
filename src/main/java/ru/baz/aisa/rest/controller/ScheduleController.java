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

    @PutMapping("/schedule")
    public ResponseEntity<SlotResponse> save(@RequestBody ScheduleServiceRequest request) {
        SlotResponse slotResponse = scheduleService.addNewSlot(request);
        return new ResponseEntity<>(slotResponse, HttpStatus.OK);
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(@RequestParam("days") Integer days) {
        List<SlotResponse> slotResponses = scheduleService.getSlotsForPeriod(days);
        return new ResponseEntity<>(slotResponses, HttpStatus.OK);
    }

    @GetMapping("/schedule/user/{id}")
    public ResponseEntity<List<SlotResponse>> getSlotsForUser(@PathVariable Long id) {
        List<SlotResponse> slotResponses = scheduleService.getSlotsForUser(id);
        return new ResponseEntity<>(slotResponses, HttpStatus.OK);
    }
}
