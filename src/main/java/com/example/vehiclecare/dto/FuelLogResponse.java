package com.example.vehiclecare.dto;

import com.example.vehiclecare.model.FuelLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record FuelLogResponse(
        Long id,
        LocalDate fillDate,
        int odometer,
        double quantity,
        BigDecimal totalCost,
        BigDecimal pricePerUnit,
        boolean fullTank
) {
    public static FuelLogResponse from(FuelLog f) {
        BigDecimal pricePerUnit = f.getQuantity() > 0
                ? f.getTotalCost().divide(BigDecimal.valueOf(f.getQuantity()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        return new FuelLogResponse(f.getId(), f.getFillDate(), f.getOdometer(), f.getQuantity(),
                f.getTotalCost(), pricePerUnit, f.isFullTank());
    }
}
