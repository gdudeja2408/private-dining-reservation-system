package com.example.privatedining.controller;

import com.example.privatedining.model.Room;
import com.example.privatedining.repository.ReservationRepository;
import com.example.privatedining.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Room testRoom;

    @BeforeEach
    void setup() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        testRoom = new Room("rest1", "Private Hall", "hall", 4, 12, 500, "AUD");
        roomRepository.save(testRoom);
    }

    @Test
    void shouldCreateReservationSuccessfully() throws Exception {
        String json = """
            {
              "roomId": "%s",
              "restaurantId": "rest1",
              "dinerName": "Gary Dudeja",
              "email": "gary@example.com",
              "partySize": 6,
              "reservationDate": "2025-11-05",
              "startTime": "18:00",
              "endTime": "20:00"
            }
            """.formatted(testRoom.getId());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.email").value("gary@example.com"));
    }

    @Test
    void shouldRejectInvalidPartySize() throws Exception {
        String json = """
            {
              "roomId": "%s",
              "restaurantId": "rest1",
              "dinerName": "John",
              "email": "john@example.com",
              "partySize": 2,
              "reservationDate": "2025-11-05",
              "startTime": "18:00",
              "endTime": "20:00"
            }
            """.formatted(testRoom.getId());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Party size 2 is invalid")));
    }

    @Test
    void shouldReturnConflictWhenDoubleBookingSameSlot() throws Exception {
        String json = """
            {
              "roomId": "%s",
              "restaurantId": "rest1",
              "dinerName": "Alice",
              "email": "alice@example.com",
              "partySize": 6,
              "reservationDate": "2025-11-06",
              "startTime": "19:00",
              "endTime": "21:00"
            }
            """.formatted(testRoom.getId());

        // First booking succeeds
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // Second booking (same room/date/slot) must fail with 409
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("already booked")));
    }
}
