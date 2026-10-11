package com.example.vehiclecare.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FuelLogRequest(
        @NotNull(message = "fillDate is required (yyyy-MM-dd)") @PastOrPresent LocalDate fillDate,
        @NotNull(message = "odometer is required") @PositiveOrZero Integer odometer,
        @NotNull(message = "quantity is required") @Positive Double quantity,
        @NotNull(message = "totalCost is required") @PositiveOrZero BigDecimal totalCost,
        Boolean fullTank
) {}
