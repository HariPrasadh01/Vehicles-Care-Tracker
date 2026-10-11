package com.example.vehiclecare.service;

import com.example.vehiclecare.dto.DocumentRequest;
import com.example.vehiclecare.dto.DocumentResponse;
import com.example.vehiclecare.exception.NotFoundException;
import com.example.vehiclecare.model.Vehicle;
import com.example.vehiclecare.model.VehicleDocument;
import com.example.vehiclecare.repository.VehicleDocumentRepository;
import com.example.vehiclecare.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DocumentService {

    private final VehicleRepository vehicles;
    private final VehicleDocumentRepository documents;

    public DocumentService(VehicleRepository vehicles, VehicleDocumentRepository documents) {
        this.vehicles = vehicles;
        this.documents = documents;
    }

    public DocumentResponse add(Long vehicleId, DocumentRequest req) {
        Vehicle vehicle = vehicles.require(vehicleId);
        VehicleDocument d = new VehicleDocument();
        d.setVehicle(vehicle);
        apply(d, req);
        return DocumentResponse.from(documents.save(d));
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> list(Long vehicleId) {
        vehicles.require(vehicleId);
        return documents.findByVehicleIdOrderByExpiryDateAsc(vehicleId)
                .stream().map(DocumentResponse::from).toList();
    }

    /** Used to renew: send the same document with a new expiry date. */
    public DocumentResponse update(Long vehicleId, Long id, DocumentRequest req) {
        VehicleDocument d = documents.findByIdAndVehicleId(id, vehicleId)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
        apply(d, req);
        return DocumentResponse.from(documents.save(d));
    }

    public void delete(Long vehicleId, Long id) {
        VehicleDocument d = documents.findByIdAndVehicleId(id, vehicleId)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
        documents.delete(d);
    }

    private void apply(VehicleDocument d, DocumentRequest req) {
        d.setDocumentType(req.documentType());
        d.setExpiryDate(req.expiryDate());
        d.setReferenceNumber(req.referenceNumber());
        d.setNotes(req.notes());
        d.setRemindDaysBefore(req.remindDaysBefore() != null ? req.remindDaysBefore() : 30);
    }
}
