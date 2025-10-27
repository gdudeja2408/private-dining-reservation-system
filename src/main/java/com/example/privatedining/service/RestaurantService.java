package com.example.privatedining.service;

import com.example.privatedining.model.Restaurant;
import com.example.privatedining.model.Room;
import com.example.privatedining.repository.RestaurantRepository;
import com.example.privatedining.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RoomRepository roomRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, RoomRepository roomRepository) {
        this.restaurantRepository = restaurantRepository;
        this.roomRepository = roomRepository;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Room> getRoomsByRestaurant(String restaurantId) {
        return roomRepository.findByRestaurantId(restaurantId);
    }
}
