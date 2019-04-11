package com.hushproject.hush;

public class UserLocations {

    private String locationName = "";
    private String locationAddress = "";
    private double locationLat = 0;
    private double locationLng = 0;
    private int locationRad = 0;
    private int locRingVol = 0;
    private int locMediVol = 0;
    private int locNotiVol = 0;
    private int locSystVol = 0;

    //constructor.
    public UserLocations(String name, double lat, double lng, int rad, int ring, int medi, int noti, int syst) {

       locationName = name;
       locationLat = lat;
       locationLng = lng;
       locationRad = rad;
       locRingVol = ring;
       locMediVol = medi;
       locNotiVol = noti;
       locSystVol = syst;
    }

    public String getLocationName() {
        return locationName;
    }

    public  String getAddress() {
        locationAddress = "Latitude: " + Double.toString(getLocationLat()) + " Longitude: "
                + Double.toString(getLocationLng()) + " Radius: "
                + Integer.toString(getLocationRad());
        return locationAddress;
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

    public int getLocNotiVol() {
        return locNotiVol;
    }

    public int getLocSystVol() {
        return locSystVol;
    }
}
