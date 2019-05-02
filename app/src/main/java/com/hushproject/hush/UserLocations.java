package com.hushproject.hush;

class UserLocations {

    private String locationName;
    private String locationAddress;
    private double locationLat;
    private double locationLng;
    private int locationRad;
    private int locRingVol;
    private int locMediVol;

    //constructor.
    UserLocations(String name,String address, double lat, double lng, int rad, int ring, int medi) {

       this.locationName = name;
       this.locationAddress = address;
       this.locationLat = lat;
       this.locationLng = lng;
       this.locationRad = rad;
       this.locRingVol = ring;
       this.locMediVol = medi;
    }

    String getLocationName() {
        return locationName;
    }

    double getLocationLat() {
        return locationLat;
    }

    double getLocationLng() {
        return locationLng;
    }

    int getLocationRad() {
        return locationRad;
    }

    int getLocRingVol() {
        return locRingVol;
    }

    int getLocMediVol() {
        return locMediVol;
    }

    String getLocationAddress() {
        return locationAddress;
    }


}
