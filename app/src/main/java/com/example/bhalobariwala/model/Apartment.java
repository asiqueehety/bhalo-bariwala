package com.example.bhalobariwala.model;

public class Apartment {
    private final long aptId;
    private final long propId;
    private final double rent;

    public Apartment(long aptId, long propId, double rent) {
        this.aptId = aptId;
        this.propId = propId;
        this.rent = rent;
    }

    public long getAptId() { return aptId; }
    public long getPropId() { return propId; }
    public double getRent() { return rent; }
}

