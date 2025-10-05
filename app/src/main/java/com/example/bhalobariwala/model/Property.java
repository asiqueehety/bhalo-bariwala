package com.example.bhalobariwala.model;

public class Property {
    private final long propId;
    private final String propName;
    private final long landlordId; // l_id

    public Property(long propId, String propName, long landlordId) {
        this.propId = propId;
        this.propName = propName;
        this.landlordId = landlordId;
    }

    public long getPropId()   { return propId; }
    public String getPropName(){ return propName; }
    public long getLandlordId(){ return landlordId; }
}
