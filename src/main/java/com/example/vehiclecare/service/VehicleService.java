package com.example.vehiclecare.service;

import com.example.vehiclecare.dto.VehicleRequest;
import com.example.vehiclecare.dto.VehicleResponse;
import com.example.vehiclecare.dto.VehicleSummary;
import com.example.vehiclecare.model.FuelLog;
import com.example.vehiclecare.model.ServiceRecord;
import com.example.vehiclecare.model.Vehicle;
import com.example.vehiclecare.repository.FuelLogRepository;
import com.example.vehiclecare.repository.ServiceRecordRepository;
import com.example.vehiclecare.repository.VehicleDocumentRepository;
import com.example.vehiclecare.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicles;
    private final FuelLogRepository fuelLogs;
    private final ServiceRecordRepository serviceRecords;
    private final VehicleDocumentRepository documents;

    public VehicleService(VehicleRepository vehicles, FuelLogRepository fuelLogs,
                          ServiceRecordRepository serviceRecords, VehicleDocumentRepository documents) {
        this.vehicles = vehicles;
        this.fuelLogs = fuelLogs;
        this.serviceRecords = serviceRecords;
        this.documents = documents;
    }

    public VehicleResponse create(VehicleRequest req) {
        Vehicle v = new Vehicle();
        apply(v, req);
        return VehicleResponse.from(vehicles.save(v));
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> list() {
        return vehicles.findAllByOrderByNameAsc().stream().map(VehicleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponse get(Long id) {
        return VehicleResponse.from(vehicles.require(id));
    }

    public VehicleResponse update(Long id, VehicleRequest req) {
        Vehicle v = vehicles.require(id);
        apply(v, req);
        return VehicleResponse.from(vehicles.save(v));
    }

    /** Deletes the vehicle together with its fuel logs, service records and documents. */
    public void delete(Long id) {
        Vehicle v = vehicles.require(id);
        fuelLogs.deleteByVehicleId(id);
        serviceRecords.deleteByVehicleId(id);
        documents.deleteByVehicleId(id);
        vehicles.delete(v);
    }

    @Transactional(readOnly = true)
    public VehicleSummary summary(Long id) {
        Vehicle v = vehicles.require(id);
        List<FuelLog> fuel = fuelLogs.findByVehicleIdOrderByOdometerAscIdAsc(id);
        List<ServiceRecord> services = serviceRecords.findByVehicleIdOrderByServiceDateDescIdDesc(id);

        MileageCalculator.Result mileage = MileageCalculator.calculate(
                fuel.stream()
                        .map(f -> new MileageCalculator.Fill(f.getOdometer(), f.getQuantity(), f.isFullTank()))
                        .toList());

        BigDecimal fuelCost = fuel.stream().map(FuelLog::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal serviceCost = services.stream()
                .map(ServiceRecord::getCost)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Fuel cost per km: the first fill only sets the starting point, so its cost is excluded.
        BigDecimal fuelCostPerKm = null;
        if (fuel.size() >= 2) {
            int distance = fuel.get(fuel.size() - 1).getOdometer() - fuel.get(0).getOdometer();
            if (distance > 0) {
                BigDecimal costAfterFirst = fuel.stream().skip(1)
                        .map(FuelLog::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
                fuelCostPerKm = costAfterFirst.divide(BigDecimal.valueOf(distance), 2, RoundingMode.HALF_UP);
            }
        }

        return new VehicleSummary(v.getId(), v.getName(), v.getCurrentOdometer(), fuel.size(),
                fuelCost, serviceCost, fuelCost.add(serviceCost),
                mileage.averageMileage(), mileage.lastMileage(), mileage.distanceTracked(), fuelCostPerKm);
    }

    private void apply(Vehicle v, VehicleRequest req) {
        v.setName(req.name().trim());
        v.setVehicleType(req.type());
        v.setRegistrationNumber(blankToNull(req.registrationNumber()));
        v.setBrand(blankToNull(req.brand()));
        v.setModelName(blankToNull(req.modelName()));
        v.setManufactureYear(req.manufactureYear());
        v.setFuelType(req.fuelType());
        if (req.currentOdometer() != null) {
            v.setCurrentOdometer(req.currentOdometer());
        }
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
