# Private Dining Reservation System

A scalable, event-driven backend application built with **Java 17** and **Spring Boot 3**, providing APIs to manage restaurants, dining rooms, and reservations.  
This version uses an **embedded in-memory MongoDB**, so no external database setup is required — making it ideal for demos, tests, and take-home assignments.

---

## Features

- **Restaurant & Room APIs**
    - Retrieve restaurants and their dining rooms.
- **Reservation APIs**
    - Create, view, and cancel reservations.
    - Enforces room capacity and time-slot overlap rules.
- **Concurrency Control**
    - Prevents double bookings using a unique compound index.
- **Event-Driven Notifications**
    - Publishes `ReservationCreatedEvent` upon successful booking.
- **In-Memory MongoDB**
    - Automatically starts an embedded MongoDB for seamless development and testing.

---

## Technology Stack

| Component | Technology                         |
|------------|------------------------------------|
| Language | Java 17                            |
| Framework | Spring Boot 3.x                    |
| Database | Embedded MongoDB (Flapdoodle)      |
| Testing | JUnit 5, Spring Boot Test, MockMvc |
| Build Tool | Gradle (Kotlin DSL)                      |

---

## Setup & Build Instructions

### 1️⃣ Clone the Repository

---

### 2️⃣ Build the Project

#### Using Gradle
```
./gradlew clean build -x test
```

---

### 3️⃣ Run the Application

#### Gradle
```
./gradlew clean bootRun
```

✅ **No MongoDB setup required!**  
An embedded MongoDB instance will start automatically during application startup.

---

### 4️⃣ Verify Startup

Expected console output:
```
 ✅ Seed data loaded successfully
```

Visit: [http://localhost:8080/api/restaurants](http://localhost:8080/api/restaurants)

### Test APIs
Import APIs into **Postman** using the file: `postman-collections.json` (Import → Raw Text → Paste JSON)
or
Visit Swagger UI 
---

## API Endpoints

### Restaurant APIs
| Method | Endpoint | Description |
|---------|-----------|-------------|
| `GET` | `/api/restaurants` | List all restaurants |
| `GET` | `/api/restaurants/{id}/rooms` | Get all rooms for a restaurant |

### Reservation APIs
| Method | Endpoint | Description |
|---------|-----------|-------------|
| `POST` | `/api/reservations` | Create a new reservation |
| `GET` | `/api/reservations?email={email}` | Get reservations by diner email |
| `GET` | `/api/reservations/all` | Get all reservations (staff only) |
| `DELETE` | `/api/reservations/{id}` | Cancel a reservation |


---

## Testing

### Run All Tests
```
./gradlew test
```

Includes:
- **Unit Tests** for business logic (`ReservationServiceTest`)
- **Integration Tests** for controllers (`ReservationControllerIntegrationTest`)
- **Concurrency Tests** (`ReservationConcurrencyTest`) ensuring only one booking succeeds under race conditions.

All tests use **embedded MongoDB**, so they are fully self-contained.

---
