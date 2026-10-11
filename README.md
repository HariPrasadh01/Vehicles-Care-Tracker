# Vehicle Care Tracker (Java + Spring Boot)

Track fuel fills and mileage, service history, document expiry (insurance, pollution certificate, etc.) and get
reminders, for one or more vehicles. Includes a REST API, a daily reminder job, and a simple web UI.

## Run
Requires Java 17+ and Maven (or open the folder in IntelliJ IDEA / VS Code and run `VehicleCareApplication`).

    mvn spring-boot:run

- Web UI: http://localhost:8080
- Database console: http://localhost:8080/h2-console  (JDBC URL `jdbc:h2:file:./data/vehiclecare`, user `sa`, empty password)
- Tests: `mvn test`

## What it does
- **Vehicles:** car, bike, scooter, etc., with a running odometer that advances automatically.
- **Fuel log:** date, odometer, litres (kg for CNG), cost, full-tank flag.
- **Mileage:** computed between full-tank fills. The first full fill is only the starting point; partial fills
  between two full fills are added to the fuel used. Also shows fuel cost per km and totals.
- **Service records:** oil change, tyres, battery, etc. Optionally set the next due date and/or next due km.
- **Documents:** insurance, pollution certificate, road tax... with an expiry date and "remind N days before". Renew by updating the expiry.
- **Reminders:** documents by expiry date, services by date and/or km. Status is `OK`, `DUE_SOON` or `OVERDUE`.
  Only the latest record of each service type counts, so logging a new oil change replaces the old reminder.
- **Daily digest:** at 08:00 (`app.reminder.cron`) everything `DUE_SOON` or `OVERDUE` is logged, and emailed if configured.

## API
| Method | Path | Purpose |
|---|---|---|
| POST / GET | /api/vehicles | add / list vehicles |
| GET / PUT / DELETE | /api/vehicles/{id} | one vehicle (delete removes all its records) |
| GET | /api/vehicles/{id}/summary | mileage, costs, cost per km |
| POST / GET | /api/vehicles/{id}/fuel | add / list fuel fills |
| DELETE | /api/vehicles/{id}/fuel/{fuelId} | remove a fill |
| POST / GET | /api/vehicles/{id}/services | add / list service records |
| DELETE | /api/vehicles/{id}/services/{serviceId} | remove a record |
| POST / GET | /api/vehicles/{id}/documents | add / list documents |
| PUT / DELETE | /api/vehicles/{id}/documents/{docId} | renew (update expiry) / remove |
| GET | /api/reminders?vehicleId=1&onlyDue=true | upcoming items, most urgent first |

Enums: vehicle `type` = CAR, BIKE, SCOOTER, AUTO, TRUCK, OTHER; `fuelType` = PETROL, DIESEL, CNG, LPG, OTHER;
`serviceType` = GENERAL_SERVICE, OIL_CHANGE, TYRE, BATTERY, BRAKE, REPAIR, OTHER;
`documentType` = INSURANCE, POLLUTION_CERTIFICATE, REGISTRATION, FITNESS, ROAD_TAX, DRIVING_LICENSE, OTHER.

Example:

    curl -X POST localhost:8080/api/vehicles -H "Content-Type: application/json" \
      -d '{"name":"My Swift","type":"CAR","fuelType":"PETROL","currentOdometer":12000}'

    curl -X POST localhost:8080/api/vehicles/1/fuel -H "Content-Type: application/json" \
      -d '{"fillDate":"2026-10-01","odometer":12100,"quantity":30.5,"totalCost":3050,"fullTank":true}'

## Email reminders
Reminders are always logged. To also receive email, uncomment the `spring.mail.*` and `app.notify.email`
lines in `src/main/resources/application.properties` (for Gmail, use an app password).

## Tuning
`app.reminder.service-days-ahead` (default 30) and `app.reminder.service-km-ahead` (default 500) control when a
service becomes `DUE_SOON`.

## Project layout
`model` (JPA entities) · `repository` · `service` (business logic, `MileageCalculator` is plain Java) ·
`controller` (REST) · `dto` · `exception` · `scheduler` · `static/index.html` (UI)

## Next steps
User accounts (Spring Security + JWT), PostgreSQL, Docker, push notifications, receipt photo upload, charts.
