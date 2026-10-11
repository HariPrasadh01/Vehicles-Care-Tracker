package com.example.vehiclecare.dto;

import com.example.vehiclecare.model.DocumentType;
import com.example.vehiclecare.model.VehicleDocument;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DocumentResponse(
        Long id,
        DocumentType documentType,
        LocalDate expiryDate,
        String referenceNumber,
        String notes,
        int remindDaysBefore,
        long daysLeft,
        String status
) {
    public static DocumentResponse from(VehicleDocument d) {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), d.getExpiryDate());
        String status = daysLeft < 0 ? "EXPIRED" : daysLeft <= d.getRemindDaysBefore() ? "EXPIRING_SOON" : "OK";
        return new DocumentResponse(d.getId(), d.getDocumentType(), d.getExpiryDate(), d.getReferenceNumber(),
                d.getNotes(), d.getRemindDaysBefore(), daysLeft, status);
    }
}
