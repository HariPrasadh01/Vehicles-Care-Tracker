package com.example.vehiclecare.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fuel_logs")
public class FuelLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDate fillDate;

    /** Odometer reading (km) at the time of filling. */
    @Column(nullable = false)
    private int odometer;

    /** Litres (kg for CNG/LPG). */
    @Column(nullable = false)
    private double quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCost;

    /** Was the tank filled to full? Mileage is only computed between full-tank fills. */
    @Column(nullable = false)
    private boolean fullTank = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public LocalDate getFillDate() { return fillDate; }
    public void setFillDate(LocalDate fillDate) { this.fillDate = fillDate; }
    public int getOdometer() { return odometer; }
    public void setOdometer(int odometer) { this.odometer = odometer; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public boolean isFullTank() { return fullTank; }
    public void setFullTank(boolean fullTank) { this.fullTank = fullTank; }
}
