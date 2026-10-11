package com.example.vehiclecare.dto;

import com.example.vehiclecare.model.ServiceRecord;
import com.example.vehiclecare.model.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ServiceRecordResponse(
        Long id,
        ServiceType serviceType,
        LocalDate serviceDate,
        int odometer,
        BigDecimal cost,
        String description,
        LocalDate nextDueDate,
        Integer nextDueOdometer
) {
    public static ServiceRecordResponse from(ServiceRecord s) {
        return new ServiceRecordResponse(s.getId(), s.getServiceType(), s.getServiceDate(), s.getOdometer(),
                s.getCost(), s.getDescription(), s.getNextDueDate(), s.getNextDueOdometer());
    }
}
