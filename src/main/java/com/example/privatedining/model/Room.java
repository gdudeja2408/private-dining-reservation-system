package com.example.privatedining.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rooms")
public class Room {
    @Id
    private String id;
    private String restaurantId;
    private String name;
    private String type;
    private int minCapacity;
    private int maxCapacity;
    private double minSpend;
    private String currency;

    public Room() {}

    public Room(String restaurantId, String name, String type,
                int minCapacity, int maxCapacity, double minSpend, String currency) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.type = type;
        this.minCapacity = minCapacity;
        this.maxCapacity = maxCapacity;
        this.minSpend = minSpend;
        this.currency = currency;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getMinCapacity() { return minCapacity; }
    public void setMinCapacity(int minCapacity) { this.minCapacity = minCapacity; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public double getMinSpend() { return minSpend; }
    public void setMinSpend(double minSpend) { this.minSpend = minSpend; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
