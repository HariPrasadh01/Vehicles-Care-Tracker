package com.example.vehiclecare.service;

import com.example.vehiclecare.dto.ReminderResponse;
import com.example.vehiclecare.model.ServiceRecord;
import com.example.vehiclecare.model.ServiceType;
import com.example.vehiclecare.model.Vehicle;
import com.example.vehiclecare.model.VehicleDocument;
import com.example.vehiclecare.repository.ServiceRecordRepository;
import com.example.vehiclecare.repository.VehicleDocumentRepository;
import com.example.vehiclecare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the list of upcoming obligations across documents (by expiry date) and services (by date and/or km).
 */
@Service
@Transactional(readOnly = true)
public class ReminderService {

    public static final String OK = "OK";
    public static final String DUE_SOON = "DUE_SOON";
    public static final String OVERDUE = "OVERDUE";

    private final VehicleRepository vehicles;
    private final ServiceRecordRepository serviceRecords;
    private final VehicleDocumentRepository documents;
    private final int serviceDaysAhead;
    private final int serviceKmAhead;

    public ReminderService(VehicleRepository vehicles,
                           ServiceRecordRepository serviceRecords,
                           VehicleDocumentRepository documents,
                           @Value("${app.reminder.service-days-ahead:30}") int serviceDaysAhead,
                           @Value("${app.reminder.service-km-ahead:500}") int serviceKmAhead) {
        this.vehicles = vehicles;
        this.serviceRecords = serviceRecords;
        this.documents = documents;
        this.serviceDaysAhead = serviceDaysAhead;
        this.serviceKmAhead = serviceKmAhead;
    }

    /**
     * @param vehicleId limit to one vehicle, or null for all vehicles
     * @param onlyDue   when true, drop items whose status is OK
     */
    public List<ReminderResponse> list(Long vehicleId, boolean onlyDue) {
        List<Vehicle> targets = vehicleId == null
                ? vehicles.findAllByOrderByNameAsc()
                : List.of(vehicles.require(vehicleId));
        LocalDate today = LocalDate.now();

        List<ReminderResponse> out = new ArrayList<>();
        for (Vehicle v : targets) {
            out.addAll(documentReminders(v, today));
            out.addAll(serviceReminders(v, today));
        }
        if (onlyDue) {
            out.removeIf(r -> OK.equals(r.status()));
        }
        out.sort(Comparator.comparingInt((ReminderResponse r) -> rank(r.status()))
                .thenComparing(ReminderResponse::dueDate, Comparator.nullsLast(Comparator.<LocalDate>naturalOrder())));
        return out;
    }

    private List<ReminderResponse> documentReminders(Vehicle v, LocalDate today) {
        List<ReminderResponse> out = new ArrayList<>();
        for (VehicleDocument d : documents.findByVehicleIdOrderByExpiryDateAsc(v.getId())) {
            long daysLeft = ChronoUnit.DAYS.between(today, d.getExpiryDate());
            String status = daysLeft < 0 ? OVERDUE : daysLeft <= d.getRemindDaysBefore() ? DUE_SOON : OK;
            String title = pretty(d.getDocumentType()) + (daysLeft < 0 ? " expired" : " expires");
            out.add(new ReminderResponse(v.getId(), v.getName(), "DOCUMENT", title,
                    d.getExpiryDate(), null, daysLeft, null, status));
        }
        return out;
    }

    private List<ReminderResponse> serviceReminders(Vehicle v, LocalDate today) {
        // newest first, so the first record we see per type is that type's latest service
        Map<ServiceType, ServiceRecord> latest = new EnumMap<>(ServiceType.class);
        for (ServiceRecord r : serviceRecords.findByVehicleIdOrderByServiceDateDescIdDesc(v.getId())) {
            latest.putIfAbsent(r.getServiceType(), r);
        }

        List<ReminderResponse> out = new ArrayList<>();
        for (ServiceRecord r : latest.values()) {
            if (r.getNextDueDate() == null && r.getNextDueOdometer() == null) {
                continue;
            }
            Long daysLeft = r.getNextDueDate() == null ? null : ChronoUnit.DAYS.between(today, r.getNextDueDate());
            Integer kmLeft = r.getNextDueOdometer() == null ? null : r.getNextDueOdometer() - v.getCurrentOdometer();

            String status;
            if ((daysLeft != null && daysLeft < 0) || (kmLeft != null && kmLeft <= 0)) {
                status = OVERDUE;
            } else if ((daysLeft != null && daysLeft <= serviceDaysAhead) || (kmLeft != null && kmLeft <= serviceKmAhead)) {
                status = DUE_SOON;
            } else {
                status = OK;
            }
            out.add(new ReminderResponse(v.getId(), v.getName(), "SERVICE", pretty(r.getServiceType()) + " due",
                    r.getNextDueDate(), r.getNextDueOdometer(), daysLeft, kmLeft, status));
        }
        return out;
    }

    private static int rank(String status) {
        return switch (status) {
            case OVERDUE -> 0;
            case DUE_SOON -> 1;
            default -> 2;
        };
    }

    private static String pretty(Enum<?> e) {
        String s = e.name().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
