package com.example.privatedining.repository;

import com.example.privatedining.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByRestaurantId(String restaurantId);

}
