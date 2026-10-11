package com.example.vehiclecare.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Full-tank-to-full-tank mileage calculation (pure Java, no Spring).
 *
 * Mileage is only meaningful between two full-tank fills: the fuel added at the later fill (plus any partial
 * fills in between) is what was burnt over the distance between them. The first full fill only sets the
 * starting point, and partial fills before it are ignored because the starting tank level is unknown.
 */
public final class MileageCalculator {

    private MileageCalculator() {}

    public record Fill(int odometer, double quantity, boolean fullTank) {}

    public record Result(Double averageMileage, Double lastMileage, int distanceTracked, double quantityTracked) {}

    public static Result calculate(List<Fill> input) {
        List<Fill> fills = new ArrayList<>(input);
        fills.sort(Comparator.comparingInt(Fill::odometer));

        Integer lastFullOdometer = null;
        double quantitySince = 0;
        int totalDistance = 0;
        double totalQuantity = 0;
        double last = 0;

        for (Fill fill : fills) {
            if (lastFullOdometer == null) {
                if (fill.fullTank()) {
                    lastFullOdometer = fill.odometer();
                    quantitySince = 0;
                }
                continue;
            }
            quantitySince += fill.quantity();
            if (fill.fullTank()) {
                int distance = fill.odometer() - lastFullOdometer;
                if (distance > 0 && quantitySince > 0) {
                    totalDistance += distance;
                    totalQuantity += quantitySince;
                    last = distance / quantitySince;
                }
                lastFullOdometer = fill.odometer();
                quantitySince = 0;
            }
        }

        if (totalQuantity <= 0) {
            return new Result(null, null, 0, 0);
        }
        return new Result(round2(totalDistance / totalQuantity), round2(last), totalDistance, round2(totalQuantity));
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
