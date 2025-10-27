package com.example.privatedining.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "restaurants")
public class Restaurant {
    @Id
    private String id;
    private String name;
    private String address;
    private String contactEmail;
    private List<String> roomIds;

    public Restaurant() {}

    public Restaurant(String name, String address, String contactEmail, List<String> roomIds) {
        this.name = name;
        this.address = address;
        this.contactEmail = contactEmail;
        this.roomIds = roomIds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public List<String> getRoomIds() { return roomIds; }
    public void setRoomIds(List<String> roomIds) { this.roomIds = roomIds; }
}
