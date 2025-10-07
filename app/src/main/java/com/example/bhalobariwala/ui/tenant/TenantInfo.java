package com.example.bhalobariwala.ui.tenant;

public class TenantInfo {
    private String name;
    private String contact;
    private int aptId;

    public TenantInfo(String name, String contact, int aptId) {
        this.name = name;
        this.contact = contact;
        this.aptId = aptId;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public int getAptId() {
        return aptId;
    }
}

