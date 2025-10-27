package com.example.privatedining.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "reservations")
@CompoundIndex(
        name = "unique_room_slot",
        def = "{'roomId': 1, 'reservationDate': 1, 'startTime': 1, 'endTime': 1, 'status': 1}",
        unique = true
)
public class Reservation {

    @Id
    private String id;

    @NotBlank(message = "Room ID is required")
    private String roomId;

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotBlank(message = "Diner name is required")
    private String dinerName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Positive(message = "Party size must be greater than zero")
    private int partySize;

    @NotNull(message = "Reservation date is required")
    private LocalDate reservationDate;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String status; // CONFIRMED or CANCELLED

    public Reservation() {}

    public Reservation(String roomId, String restaurantId, String dinerName, String email,
                       int partySize, LocalDate reservationDate,
                       LocalTime startTime, LocalTime endTime, String status) {
        this.roomId = roomId;
        this.restaurantId = restaurantId;
        this.dinerName = dinerName;
        this.email = email;
        this.partySize = partySize;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getDinerName() { return dinerName; }
    public void setDinerName(String dinerName) { this.dinerName = dinerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getPartySize() { return partySize; }
    public void setPartySize(int partySize) { this.partySize = partySize; }

    public LocalDate getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
