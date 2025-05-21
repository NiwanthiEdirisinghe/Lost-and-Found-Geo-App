package com.example.lostandfoundappbygeo;

import java.io.Serializable;

public class Item implements Serializable {
    private int id;
    private String name;
    private String phone;
    private String description;
    private String date;
    private String location;
    private String type; // "Lost" or "Found
    private double latitude = 0.0;
    private double longitude = 0.0;

    public Item() {
    }

    public Item(String name, String phone, String description, String date, String location, String type) {
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
        this.type = type;
    }

    public Item(String name, String phone, String description, String date, String location, String type, double latitude, double longitude) {
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return type + " " + description;
    }

}