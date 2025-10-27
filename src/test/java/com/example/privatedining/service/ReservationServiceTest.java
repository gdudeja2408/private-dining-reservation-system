package com.example.privatedining.service;

import com.example.privatedining.event.ReservationCreatedEvent;
import com.example.privatedining.exception.BadRequestException;
import com.example.privatedining.exception.ReservationConflictException;
import com.example.privatedining.model.Reservation;
import com.example.privatedining.model.Room;
import com.example.privatedining.repository.ReservationRepository;
import com.example.privatedining.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationRepository reservationRepo;
    private RoomRepository roomRepo;
    private ApplicationEventPublisher publisher;
    private ReservationService service;

    @BeforeEach
    void setup() {
        reservationRepo = mock(ReservationRepository.class);
        roomRepo = mock(RoomRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        service = new ReservationService(reservationRepo, roomRepo, publisher);
    }

    @Test
    void shouldThrowBadRequestWhenPartyTooSmall() {
        Room room = new Room("rest1", "Rooftop", "rooftop", 4, 10, 200, "AUD");
        when(roomRepo.findById("room1")).thenReturn(Optional.of(room));

        Reservation res = new Reservation(
                "room1", "rest1", "John", "john@x.com",
                2, LocalDate.of(2025, 11, 1),
                LocalTime.of(18, 0), LocalTime.of(20, 0),
                null
        );

        assertThrows(BadRequestException.class, () -> service.createReservation(res));
    }

    @Test
    void shouldThrowConflictWhenDuplicateKeyExceptionOccurs() {
        Room room = new Room("rest1", "Hall", "hall", 2, 10, 200, "AUD");
        when(roomRepo.findById("room1")).thenReturn(Optional.of(room));
        when(reservationRepo.save(any())).thenThrow(new DuplicateKeyException("duplicate"));

        Reservation res = new Reservation(
                "room1", "rest1", "Jane", "jane@x.com",
                4, LocalDate.of(2025, 11, 5),
                LocalTime.of(19, 0), LocalTime.of(21, 0),
                null
        );

        assertThrows(ReservationConflictException.class, () -> service.createReservation(res));
    }

    @Test
    void shouldPublishEventOnSuccess() {
        Room room = new Room("rest1", "Hall", "hall", 2, 10, 200, "AUD");
        when(roomRepo.findById("room1")).thenReturn(Optional.of(room));
        when(reservationRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Reservation input = new Reservation(
                "room1", "rest1", "Gary", "gary@x.com",
                4, LocalDate.of(2025, 11, 5),
                LocalTime.of(18, 0), LocalTime.of(20, 0),
                null
        );

        service.createReservation(input);

        ArgumentCaptor<ReservationCreatedEvent> captor = ArgumentCaptor.forClass(ReservationCreatedEvent.class);
        verify(publisher).publishEvent(captor.capture());

        ReservationCreatedEvent event = captor.getValue();
        assertEquals("gary@x.com", event.getReservation().getEmail());
    }
}
