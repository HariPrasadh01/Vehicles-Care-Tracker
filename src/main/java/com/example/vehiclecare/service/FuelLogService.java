package com.example.vehiclecare.service;

import com.example.vehiclecare.dto.FuelLogRequest;
import com.example.vehiclecare.dto.FuelLogResponse;
import com.example.vehiclecare.exception.NotFoundException;
import com.example.vehiclecare.model.FuelLog;
import com.example.vehiclecare.model.Vehicle;
import com.example.vehiclecare.repository.FuelLogRepository;
import com.example.vehiclecare.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FuelLogService {

    private final VehicleRepository vehicles;
    private final FuelLogRepository fuelLogs;

    public FuelLogService(VehicleRepository vehicles, FuelLogRepository fuelLogs) {
        this.vehicles = vehicles;
        this.fuelLogs = fuelLogs;
    }

    public FuelLogResponse add(Long vehicleId, FuelLogRequest req) {
        Vehicle vehicle = vehicles.require(vehicleId);

        FuelLog log = new FuelLog();
        log.setVehicle(vehicle);
        log.setFillDate(req.fillDate());
        log.setOdometer(req.odometer());
        log.setQuantity(req.quantity());
        log.setTotalCost(req.totalCost());
        log.setFullTank(req.fullTank() == null || req.fullTank());

        if (req.odometer() > vehicle.getCurrentOdometer()) {
            vehicle.setCurrentOdometer(req.odometer());
        }
        return FuelLogResponse.from(fuelLogs.save(log));
    }

    @Transactional(readOnly = true)
    public List<FuelLogResponse> list(Long vehicleId) {
        vehicles.require(vehicleId);
        return fuelLogs.findByVehicleIdOrderByFillDateDescOdometerDesc(vehicleId)
                .stream().map(FuelLogResponse::from).toList();
    }

    public void delete(Long vehicleId, Long id) {
        FuelLog log = fuelLogs.findByIdAndVehicleId(id, vehicleId)
                .orElseThrow(() -> new NotFoundException("Fuel log not found: " + id));
        fuelLogs.delete(log);
    }
}
