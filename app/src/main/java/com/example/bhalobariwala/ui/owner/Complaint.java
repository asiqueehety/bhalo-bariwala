package com.example.bhalobariwala.ui.owner;

public class Complaint {
    private String title;
    private String description;
    private int propertyId;
    private String propertyName;
    private int apartmentId;
    private String type;

    public Complaint(String title, String description, int propertyId, String propertyName, int apartmentId, String type) {
        this.title = title;
        this.description = description;
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.apartmentId = apartmentId;
        this.type = type;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPropertyId() { return propertyId; }
    public String getPropertyName() { return propertyName; }
    public int getApartmentId() { return apartmentId; }
    public String getType() { return type; }
}
