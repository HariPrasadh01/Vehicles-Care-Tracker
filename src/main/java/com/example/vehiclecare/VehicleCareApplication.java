package com.example.vehiclecare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VehicleCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(VehicleCareApplication.class, args);
    }
}
