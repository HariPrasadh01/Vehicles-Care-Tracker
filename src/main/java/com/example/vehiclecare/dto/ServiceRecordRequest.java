package com.example.vehiclecare.dto;

import com.example.vehiclecare.model.ServiceType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ServiceRecordRequest(
        @NotNull(message = "serviceType is required") ServiceType serviceType,
        @NotNull(message = "serviceDate is required (yyyy-MM-dd)") @PastOrPresent LocalDate serviceDate,
        @NotNull(message = "odometer is required") @PositiveOrZero Integer odometer,
        @PositiveOrZero BigDecimal cost,
        @Size(max = 300) String description,
        LocalDate nextDueDate,
        @Positive Integer nextDueOdometer
) {}
