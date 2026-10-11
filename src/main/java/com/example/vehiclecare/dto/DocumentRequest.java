package com.example.vehiclecare.dto;

import com.example.vehiclecare.model.DocumentType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record DocumentRequest(
        @NotNull(message = "documentType is required") DocumentType documentType,
        @NotNull(message = "expiryDate is required (yyyy-MM-dd)") LocalDate expiryDate,
        @Size(max = 60) String referenceNumber,
        @Size(max = 300) String notes,
        @Min(0) @Max(365) Integer remindDaysBefore
) {}
