package com.example.privatedining.service;

import com.example.privatedining.event.ReservationCreatedEvent;
import com.example.privatedining.exception.BadRequestException;
import com.example.privatedining.exception.ReservationConflictException;
import com.example.privatedining.exception.ResourceNotFoundException;
import com.example.privatedining.model.Reservation;
import com.example.privatedining.model.Room;
import com.example.privatedining.repository.ReservationRepository;
import com.example.privatedining.repository.RoomRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomRepository roomRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.eventPublisher = eventPublisher;
    }

    public Reservation createReservation(Reservation reservation) {
        // Validate required fields
        if (reservation.getRoomId() == null ||
                reservation.getReservationDate() == null ||
                reservation.getStartTime() == null ||
                reservation.getEndTime() == null) {
            throw new BadRequestException("Missing required reservation fields.");
        }

        // Ensure end time > start time
        if (!reservation.getEndTime().isAfter(reservation.getStartTime())) {
            throw new BadRequestException("End time must be after start time.");
        }

        // Check room existence
        Room room = roomRepository.findById(reservation.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + reservation.getRoomId()
                ));

        // Ensure restaurantId matches room’s restaurantId
        if (reservation.getRestaurantId() == null ||
                !room.getRestaurantId().equals(reservation.getRestaurantId())) {
            throw new BadRequestException(String.format(
                    "Room '%s' does not belong to restaurant '%s'.",
                    room.getId(), reservation.getRestaurantId()
            ));
        }

        // Validate party size fits room capacity
        int size = reservation.getPartySize();
        if (size < room.getMinCapacity() || size > room.getMaxCapacity()) {
            throw new BadRequestException(String.format(
                    "Party size %d is invalid. Room '%s' supports between %d and %d guests.",
                    size, room.getName(), room.getMinCapacity(), room.getMaxCapacity()
            ));
        }

        // Check for overlapping reservations for the same room and date
        List<Reservation> existing = reservationRepository
                .findByRoomIdAndReservationDateAndStatus(
                        reservation.getRoomId(), reservation.getReservationDate(), "CONFIRMED");

        for (Reservation r : existing) {
            if (isOverlapping(
                    reservation.getStartTime(), reservation.getEndTime(),
                    r.getStartTime(), r.getEndTime())) {
                throw new ReservationConflictException(String.format(
                        "Room already booked for overlapping time slot: %s - %s",
                        r.getStartTime(), r.getEndTime()
                ));
            }
        }

        // Save reservation and handle DB-level conflicts
        try {
            reservation.setStatus("CONFIRMED");
            Reservation saved = reservationRepository.save(reservation);
            eventPublisher.publishEvent(new ReservationCreatedEvent(this, saved));
            return saved;
        } catch (DuplicateKeyException e) {
            throw new ReservationConflictException(
                    "Room already booked for this exact date and time range."
            );
        }
    }

    private boolean isOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        //  This correctly detects any overlap and allows adjacent times (e.g. 18:00–19:00 and 19:00–20:00)
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    public List<Reservation> getReservationsByEmail(String email) {
        return reservationRepository.findByEmail(email);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation cancelReservation(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        reservation.setStatus("CANCELLED");
        return reservationRepository.save(reservation);
    }

}
