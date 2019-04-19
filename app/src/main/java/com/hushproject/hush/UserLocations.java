package com.hushproject.hush;

public class UserLocations {

    private String locationName = "";
    private double locationLat = 0;
    private double locationLng = 0;
    private int locationRad = 0;
    private int locRingVol = 0;
    private int locMediVol = 0;

    //constructor.
    public UserLocations(String name, double lat, double lng, int rad, int ring, int medi) {

       locationName = name;
       locationLat = lat;
       locationLng = lng;
       locationRad = rad;
       locRingVol = ring;
       locMediVol = medi;
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
}
