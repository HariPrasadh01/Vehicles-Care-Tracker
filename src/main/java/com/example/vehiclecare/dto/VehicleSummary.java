package com.example.vehiclecare.dto;

import java.math.BigDecimal;

/**
 * Cost and mileage overview for one vehicle.
 * Mileage is km per litre (km per kg for CNG/LPG), computed between full-tank fills only.
 */
public record VehicleSummary(
        Long vehicleId,
        String name,
        int currentOdometer,
        int fuelFillCount,
        BigDecimal totalFuelCost,
        BigDecimal totalServiceCost,
        BigDecimal totalSpent,
        Double averageMileage,
        Double lastMileage,
        int distanceTracked,
        BigDecimal fuelCostPerKm
) {}
