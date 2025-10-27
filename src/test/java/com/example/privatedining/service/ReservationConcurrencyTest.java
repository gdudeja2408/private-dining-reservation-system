package com.example.privatedining.service;

import com.example.privatedining.exception.ReservationConflictException;
import com.example.privatedining.model.Reservation;
import com.example.privatedining.model.Room;
import com.example.privatedining.repository.ReservationRepository;
import com.example.privatedining.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationConcurrencyTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private ReservationService reservationService;
    private Room testRoom;

    @BeforeEach
    void setup() {
        roomRepository.deleteAll();
        reservationRepository.deleteAll();

        reservationService = new ReservationService(reservationRepository, roomRepository, eventPublisher);

        testRoom = new Room("rest1", "Sky Lounge", "hall", 4, 12, 600, "AUD");
        roomRepository.save(testRoom);
    }

    @Test
    void shouldPreventDoubleBookingUnderConcurrency() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        LocalDate date = LocalDate.of(2025, 11, 10);
        LocalTime start = LocalTime.of(19, 0);
        LocalTime end = LocalTime.of(21, 0);

        Reservation reservation1 = new Reservation(
                testRoom.getId(), "rest1", "Alice", "alice@test.com",
                6, date, start, end, null
        );

        Reservation reservation2 = new Reservation(
                testRoom.getId(), "rest1", "Bob", "bob@test.com",
                6, date, start, end, null
        );

        Future<?> future1 = executor.submit(() -> {
            try {
                reservationService.createReservation(reservation1);
            } finally {
                latch.countDown();
            }
        });

        Future<?> future2 = executor.submit(() -> {
            try {
                reservationService.createReservation(reservation2);
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        executor.shutdown();

        int confirmedCount = (int) reservationRepository.findAll().stream()
                .filter(r -> "CONFIRMED".equals(r.getStatus()))
                .count();

        assertEquals(1, confirmedCount, "Only one reservation should be confirmed");

        try {
            future1.get();
            future2.get();
        } catch (ExecutionException ex) {
            assertTrue(ex.getCause() instanceof ReservationConflictException,
                    "Expected ReservationConflictException for duplicate booking");
        }
    }
}
