package com.example.privatedining.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {

    @Async
    @EventListener
    public void handleReservationCreated(ReservationCreatedEvent event) {
        try {
            Thread.sleep(1000); // simulate async email delay
        } catch (InterruptedException ignored) {}

        var r = event.getReservation();
        System.out.printf(
                "📧 Email sent to %s | Reservation confirmed for %s (%s - %s)%n",
                r.getEmail(),
                r.getReservationDate(),
                r.getStartTime(),
                r.getEndTime()
        );
    }
}
