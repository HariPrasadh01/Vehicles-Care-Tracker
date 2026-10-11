package com.example.vehiclecare.controller;

import com.example.vehiclecare.dto.DocumentRequest;
import com.example.vehiclecare.dto.DocumentResponse;
import com.example.vehiclecare.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles/{vehicleId}/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse add(@PathVariable Long vehicleId, @Valid @RequestBody DocumentRequest req) {
        return service.add(vehicleId, req);
    }

    @GetMapping
    public List<DocumentResponse> list(@PathVariable Long vehicleId) {
        return service.list(vehicleId);
    }

    @PutMapping("/{id}")
    public DocumentResponse update(@PathVariable Long vehicleId, @PathVariable Long id,
                                   @Valid @RequestBody DocumentRequest req) {
        return service.update(vehicleId, id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long vehicleId, @PathVariable Long id) {
        service.delete(vehicleId, id);
    }
}
