package com.example.bhalobariwala.model;

public class Message {
    private int id;
    private int landlordId;
    private int tenantId;
    private String senderType;
    private String message;
    private long timestamp;
    private boolean isRead;

    public Message(int id, int landlordId, int tenantId, String senderType, String message, long timestamp, boolean isRead) {
        this.id = id;
        this.landlordId = landlordId;
        this.tenantId = tenantId;
        this.senderType = senderType;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public int getLandlordId() {
        return landlordId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public String getSenderType() {
        return senderType;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }
}

