package com.example.vehiclecare.controller;

import com.example.vehiclecare.dto.ReminderResponse;
import com.example.vehiclecare.service.ReminderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService service;

    public ReminderController(ReminderService service) {
        this.service = service;
    }

    /** All reminders, most urgent first. Use ?vehicleId=1 to filter and ?onlyDue=true to hide OK items. */
    @GetMapping
    public List<ReminderResponse> list(@RequestParam(required = false) Long vehicleId,
                                       @RequestParam(defaultValue = "false") boolean onlyDue) {
        return service.list(vehicleId, onlyDue);
    }
}
