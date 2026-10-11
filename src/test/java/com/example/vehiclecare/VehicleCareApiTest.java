package com.example.vehiclecare;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:vehicletest;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class VehicleCareApiTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    private long createVehicle(String name) throws Exception {
        String body = mvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"type\":\"CAR\",\"fuelType\":\"PETROL\",\"currentOdometer\":1000}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return om.readTree(body).get("id").asLong();
    }

    private void addFuel(long id, int daysAgo, int odometer, double qty, int cost, boolean full) throws Exception {
        mvc.perform(post("/api/vehicles/" + id + "/fuel").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fillDate\":\"" + LocalDate.now().minusDays(daysAgo) + "\",\"odometer\":" + odometer
                                + ",\"quantity\":" + qty + ",\"totalCost\":" + cost + ",\"fullTank\":" + full + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    void mileageIsComputedBetweenFullTankFills() throws Exception {
        long id = createVehicle("Mileage car");
        addFuel(id, 30, 1000, 30, 2400, true);   // starting point
        addFuel(id, 20, 1400, 20, 1600, true);   // 400 km / 20 L = 20
        addFuel(id, 10, 1700, 10, 800, false);   // partial
        addFuel(id, 1, 1900, 10, 800, true);     // 500 km / 20 L = 25

        mvc.perform(get("/api/vehicles/" + id + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageMileage").value(22.5))
                .andExpect(jsonPath("$.lastMileage").value(25.0))
                .andExpect(jsonPath("$.currentOdometer").value(1900))
                .andExpect(jsonPath("$.fuelFillCount").value(4));
    }

    @Test
    void serviceDueByKmShowsAsDueSoon() throws Exception {
        long id = createVehicle("Service car");
        mvc.perform(post("/api/vehicles/" + id + "/services").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceType\":\"OIL_CHANGE\",\"serviceDate\":\"" + LocalDate.now()
                                + "\",\"odometer\":1000,\"cost\":1500,\"nextDueOdometer\":1300}"))
                .andExpect(status().isCreated());

        // current odometer 1000, due at 1300 -> 300 km left, within the 500 km window
        mvc.perform(get("/api/reminders?vehicleId=" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].kind").value("SERVICE"))
                .andExpect(jsonPath("$[0].status").value("DUE_SOON"))
                .andExpect(jsonPath("$[0].kmLeft").value(300));
    }

    @Test
    void expiredDocumentIsOverdueAndRenewalClearsIt() throws Exception {
        long id = createVehicle("Doc car");
        String body = mvc.perform(post("/api/vehicles/" + id + "/documents").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"documentType\":\"INSURANCE\",\"expiryDate\":\"" + LocalDate.now().minusDays(5) + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("EXPIRED"))
                .andReturn().getResponse().getContentAsString();
        long docId = om.readTree(body).get("id").asLong();

        mvc.perform(get("/api/reminders?vehicleId=" + id + "&onlyDue=true"))
                .andExpect(jsonPath("$[0].status").value("OVERDUE"));

        mvc.perform(put("/api/vehicles/" + id + "/documents/" + docId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"documentType\":\"INSURANCE\",\"expiryDate\":\"" + LocalDate.now().plusDays(365) + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        mvc.perform(get("/api/reminders?vehicleId=" + id + "&onlyDue=true"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void rejectsInvalidInput() throws Exception {
        mvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());

        long id = createVehicle("Validation car");
        // next due odometer must be beyond the odometer at service
        mvc.perform(post("/api/vehicles/" + id + "/services").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceType\":\"TYRE\",\"serviceDate\":\"" + LocalDate.now()
                                + "\",\"odometer\":2000,\"nextDueOdometer\":1500}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownVehicleReturns404AndDeleteRemovesEverything() throws Exception {
        mvc.perform(get("/api/vehicles/99999")).andExpect(status().isNotFound());

        long id = createVehicle("Delete car");
        addFuel(id, 2, 1100, 10, 900, true);
        mvc.perform(delete("/api/vehicles/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/vehicles/" + id + "/fuel")).andExpect(status().isNotFound());
    }
}
