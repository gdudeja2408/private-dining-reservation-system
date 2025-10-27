package com.example.privatedining.config;

import com.example.privatedining.model.Reservation;
import com.example.privatedining.model.Restaurant;
import com.example.privatedining.model.Room;
import com.example.privatedining.repository.ReservationRepository;
import com.example.privatedining.repository.RestaurantRepository;
import com.example.privatedining.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final ObjectMapper mapper;

    public DataSeeder(RestaurantRepository restaurantRepository,
                      RoomRepository roomRepository,
                      ReservationRepository reservationRepository) {
        this.restaurantRepository = restaurantRepository;
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void run(String... args) {
        try {
            boolean empty = restaurantRepository.count() == 0 &&
                    roomRepository.count() == 0 &&
                    reservationRepository.count() == 0;

            if (empty) {
                System.out.println(" Loading seed data...");
                InputStream stream = new ClassPathResource("seed.json").getInputStream();
                Seed seed = mapper.readValue(stream, Seed.class);

                roomRepository.saveAll(seed.getRooms());
                restaurantRepository.saveAll(seed.getRestaurants());
                reservationRepository.saveAll(seed.getReservations());

                System.out.println("✅ Seed data loaded successfully.");
            } else {
                System.out.println("ℹ️ Seed data skipped (collections not empty).");
            }
        } catch (Exception e) {
            System.err.println("❌ Seed load failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static class Seed {
        private List<Restaurant> restaurants;
        private List<Room> rooms;
        private List<Reservation> reservations;

        public List<Restaurant> getRestaurants() { return restaurants; }
        public void setRestaurants(List<Restaurant> restaurants) { this.restaurants = restaurants; }

        public List<Room> getRooms() { return rooms; }
        public void setRooms(List<Room> rooms) { this.rooms = rooms; }

        public List<Reservation> getReservations() { return reservations; }
        public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
    }
}
