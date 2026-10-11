package com.example.vehiclecare.controller;

import com.example.vehiclecare.dto.ServiceRecordRequest;
import com.example.vehiclecare.dto.ServiceRecordResponse;
import com.example.vehiclecare.service.ServiceRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles/{vehicleId}/services")
public class ServiceRecordController {

    private final ServiceRecordService service;

    public ServiceRecordController(ServiceRecordService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRecordResponse add(@PathVariable Long vehicleId, @Valid @RequestBody ServiceRecordRequest req) {
        return service.add(vehicleId, req);
    }

    @GetMapping
    public List<ServiceRecordResponse> list(@PathVariable Long vehicleId) {
        return service.list(vehicleId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long vehicleId, @PathVariable Long id) {
        service.delete(vehicleId, id);
    }
}
