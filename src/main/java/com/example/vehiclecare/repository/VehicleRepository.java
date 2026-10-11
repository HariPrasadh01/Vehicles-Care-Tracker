package com.example.vehiclecare.repository;

import com.example.vehiclecare.exception.NotFoundException;
import com.example.vehiclecare.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findAllByOrderByNameAsc();

    default Vehicle require(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Vehicle not found: " + id));
    }
}
