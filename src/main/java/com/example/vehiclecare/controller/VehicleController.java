package com.example.vehiclecare.controller;

import com.example.vehiclecare.dto.VehicleRequest;
import com.example.vehiclecare.dto.VehicleResponse;
import com.example.vehiclecare.dto.VehicleSummary;
import com.example.vehiclecare.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse create(@Valid @RequestBody VehicleRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<VehicleResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public VehicleResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody VehicleRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/summary")
    public VehicleSummary summary(@PathVariable Long id) {
        return service.summary(id);
    }
}
