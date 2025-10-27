package com.example.privatedining.controller;

import com.example.privatedining.model.Restaurant;
import com.example.privatedining.model.Room;
import com.example.privatedining.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    //  Get all restaurants
    @GetMapping
    public ResponseEntity<List<Restaurant>> getRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    //  Get rooms for a specific restaurant
    @GetMapping("/{id}/rooms")
    public ResponseEntity<?> getRooms(@PathVariable String id) {
        List<Room> rooms = restaurantService.getRoomsByRestaurant(id);
        if (rooms.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "No rooms found for restaurant ID: " + id);
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(rooms);
    }
}
