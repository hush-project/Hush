package com.hushproject.hush;

public class UserLocations {

    private String locationName = "";



    String locationAddress = "";
    private double locationLat = 0;
    private double locationLng = 0;
    private int locationRad = 0;
    private int locRingVol = 0;
    private int locMediVol = 0;

    //constructor.
    public UserLocations(String name,String address, double lat, double lng, int rad, int ring, int medi) {

       this.locationName = name;
       this.locationAddress = address;
       this.locationLat = lat;
       this.locationLng = lng;
       this.locationRad = rad;
       this.locRingVol = ring;
       this.locMediVol = medi;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public double getLocationLng() {
        return locationLng;
    }

    public int getLocationRad() {
        return locationRad;
    }

    public int getLocRingVol() {
        return locRingVol;
    }

    public int getLocMediVol() {
        return locMediVol;
    }

    public String getLocationAddress() {
        return locationAddress;
    }


}
