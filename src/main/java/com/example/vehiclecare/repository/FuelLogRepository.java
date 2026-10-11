package com.example.vehiclecare.repository;

import com.example.vehiclecare.model.FuelLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuelLogRepository extends JpaRepository<FuelLog, Long> {

    List<FuelLog> findByVehicleIdOrderByFillDateDescOdometerDesc(Long vehicleId);

    /** Oldest first, used for mileage calculation. */
    List<FuelLog> findByVehicleIdOrderByOdometerAscIdAsc(Long vehicleId);

    Optional<FuelLog> findByIdAndVehicleId(Long id, Long vehicleId);

    void deleteByVehicleId(Long vehicleId);
}
