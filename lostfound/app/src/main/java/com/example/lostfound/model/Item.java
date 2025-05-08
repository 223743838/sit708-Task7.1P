package com.example.lostfound.model;

public class Item {
    private int id;
    private String title;
    private String description;
    private String location;
    private String date;
    private String contact;
    private String type; // Lost or Found

    public Item() {}

    public Item(String title, String description, String location, String date, String contact, String type) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.contact = contact;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}