package com.example.vehiclecare.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * One upcoming obligation: a document renewal or a service due by date and/or odometer.
 * status is OK, DUE_SOON or OVERDUE.
 */
public record ReminderResponse(
        Long vehicleId,
        String vehicleName,
        String kind,
        String title,
        LocalDate dueDate,
        Integer dueOdometer,
        Long daysLeft,
        Integer kmLeft,
        String status
) {
    /** One-line human description used in reminder notifications. */
    public String summary() {
        List<String> parts = new ArrayList<>();
        if (daysLeft != null) {
            parts.add(daysLeft < 0 ? "overdue by " + (-daysLeft) + " day(s)"
                    : daysLeft == 0 ? "due today"
                    : "due in " + daysLeft + " day(s)");
        }
        if (kmLeft != null) {
            parts.add(kmLeft <= 0 ? "odometer limit reached" : kmLeft + " km left");
        }
        return vehicleName + " - " + title + ": " + String.join(", ", parts);
    }
}
