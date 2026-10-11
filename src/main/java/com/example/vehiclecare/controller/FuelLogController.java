package com.example.vehiclecare.controller;

import com.example.vehiclecare.dto.FuelLogRequest;
import com.example.vehiclecare.dto.FuelLogResponse;
import com.example.vehiclecare.service.FuelLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles/{vehicleId}/fuel")
public class FuelLogController {

    private final FuelLogService service;

    public FuelLogController(FuelLogService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuelLogResponse add(@PathVariable Long vehicleId, @Valid @RequestBody FuelLogRequest req) {
        return service.add(vehicleId, req);
    }

    @GetMapping
    public List<FuelLogResponse> list(@PathVariable Long vehicleId) {
        return service.list(vehicleId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long vehicleId, @PathVariable Long id) {
        service.delete(vehicleId, id);
    }
}
