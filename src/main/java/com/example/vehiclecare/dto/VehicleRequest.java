package com.example.vehiclecare.dto;

import com.example.vehiclecare.model.FuelType;
import com.example.vehiclecare.model.VehicleType;
import jakarta.validation.constraints.*;

public record VehicleRequest(
        @NotBlank(message = "name is required") @Size(max = 80) String name,
        @NotNull(message = "type is required") VehicleType type,
        @Size(max = 20) String registrationNumber,
        @Size(max = 60) String brand,
        @Size(max = 60) String modelName,
        @Min(1900) @Max(2100) Integer manufactureYear,
        FuelType fuelType,
        @PositiveOrZero Integer currentOdometer
) {}
