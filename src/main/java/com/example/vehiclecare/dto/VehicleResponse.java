package com.example.vehiclecare.dto;

import com.example.vehiclecare.model.FuelType;
import com.example.vehiclecare.model.Vehicle;
import com.example.vehiclecare.model.VehicleType;

public record VehicleResponse(
        Long id,
        String name,
        VehicleType type,
        String registrationNumber,
        String brand,
        String modelName,
        Integer manufactureYear,
        FuelType fuelType,
        int currentOdometer
) {
    public static VehicleResponse from(Vehicle v) {
        return new VehicleResponse(v.getId(), v.getName(), v.getVehicleType(), v.getRegistrationNumber(),
                v.getBrand(), v.getModelName(), v.getManufactureYear(), v.getFuelType(), v.getCurrentOdometer());
    }
}
