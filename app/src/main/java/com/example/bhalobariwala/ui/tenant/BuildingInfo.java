package com.example.bhalobariwala.ui.tenant;

public class BuildingInfo {
    private String buildingName;
    private int buildingId;
    private String landlordName;
    private String landlordPhone;

    public BuildingInfo(String buildingName, int buildingId, String landlordName, String landlordPhone) {
        this.buildingName = buildingName;
        this.buildingId = buildingId;
        this.landlordName = landlordName;
        this.landlordPhone = landlordPhone;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public String getLandlordName() {
        return landlordName;
    }

    public String getLandlordPhone() {
        return landlordPhone;
    }
}

