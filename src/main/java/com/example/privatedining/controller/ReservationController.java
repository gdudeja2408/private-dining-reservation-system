package com.example.privatedining.controller;

import com.example.privatedining.model.Reservation;
import com.example.privatedining.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Diners create reservation
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody Reservation reservation) {
        Reservation created = reservationService.createReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Diners view their reservations
    @GetMapping
    public ResponseEntity<?> getReservations(@RequestParam String email) {
        List<Reservation> reservations = reservationService.getReservationsByEmail(email);
        if (reservations.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "No reservations found for email: " + email,
                    "data", List.of()
            ));
        }
        return ResponseEntity.ok(reservations);
    }

    // Staff view all
    @GetMapping("/all")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> allReservations = reservationService.getAllReservations();
        return ResponseEntity.ok(allReservations);
    }

    // Cancel
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancelReservation(@PathVariable String id) {
        Reservation cancelled = reservationService.cancelReservation(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Reservation cancelled successfully");
        response.put("reservationId", cancelled.getId());
        response.put("status", cancelled.getStatus());
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

}
