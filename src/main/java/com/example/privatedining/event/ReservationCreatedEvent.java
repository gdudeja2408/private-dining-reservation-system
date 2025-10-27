package com.example.privatedining.event;

import com.example.privatedining.model.Reservation;
import org.springframework.context.ApplicationEvent;

public class ReservationCreatedEvent extends ApplicationEvent {
    private final Reservation reservation;

    public ReservationCreatedEvent(Object source, Reservation reservation) {
        super(source);
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
