package com.example.privatedining.repository;

import com.example.privatedining.model.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> { }
