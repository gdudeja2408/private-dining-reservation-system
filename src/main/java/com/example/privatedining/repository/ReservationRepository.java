package com.example.privatedining.repository;

import com.example.privatedining.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByEmail(String email);

    List<Reservation> findByRoomIdAndReservationDateAndStatus(
            String roomId,
            LocalDate reservationDate,
            String status
    );

}
