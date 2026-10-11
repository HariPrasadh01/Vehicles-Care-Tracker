package com.example.vehiclecare.service;

import com.example.vehiclecare.dto.ServiceRecordRequest;
import com.example.vehiclecare.dto.ServiceRecordResponse;
import com.example.vehiclecare.exception.BadRequestException;
import com.example.vehiclecare.exception.NotFoundException;
import com.example.vehiclecare.model.ServiceRecord;
import com.example.vehiclecare.model.Vehicle;
import com.example.vehiclecare.repository.ServiceRecordRepository;
import com.example.vehiclecare.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceRecordService {

    private final VehicleRepository vehicles;
    private final ServiceRecordRepository records;

    public ServiceRecordService(VehicleRepository vehicles, ServiceRecordRepository records) {
        this.vehicles = vehicles;
        this.records = records;
    }

    public ServiceRecordResponse add(Long vehicleId, ServiceRecordRequest req) {
        Vehicle vehicle = vehicles.require(vehicleId);

        if (req.nextDueDate() != null && !req.nextDueDate().isAfter(req.serviceDate())) {
            throw new BadRequestException("nextDueDate must be after the service date");
        }
        if (req.nextDueOdometer() != null && req.nextDueOdometer() <= req.odometer()) {
            throw new BadRequestException("nextDueOdometer must be greater than the odometer at service");
        }

        ServiceRecord r = new ServiceRecord();
        r.setVehicle(vehicle);
        r.setServiceType(req.serviceType());
        r.setServiceDate(req.serviceDate());
        r.setOdometer(req.odometer());
        r.setCost(req.cost());
        r.setDescription(req.description());
        r.setNextDueDate(req.nextDueDate());
        r.setNextDueOdometer(req.nextDueOdometer());

        if (req.odometer() > vehicle.getCurrentOdometer()) {
            vehicle.setCurrentOdometer(req.odometer());
        }
        return ServiceRecordResponse.from(records.save(r));
    }

    @Transactional(readOnly = true)
    public List<ServiceRecordResponse> list(Long vehicleId) {
        vehicles.require(vehicleId);
        return records.findByVehicleIdOrderByServiceDateDescIdDesc(vehicleId)
                .stream().map(ServiceRecordResponse::from).toList();
    }

    public void delete(Long vehicleId, Long id) {
        ServiceRecord r = records.findByIdAndVehicleId(id, vehicleId)
                .orElseThrow(() -> new NotFoundException("Service record not found: " + id));
        records.delete(r);
    }
}
