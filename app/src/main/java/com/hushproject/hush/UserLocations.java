package com.hushproject.hush;

public class UserLocations {

    private String locationName = "";
    private String locationAddress = "";
    private int locationLat = 0;
    private int locationLng = 0;
    private int locationRad = 0;
    private int locRingVol = 0;
    private int locMediVol = 0;
    private int locNotiVol = 0;
    private int locSystVol = 0;

    //constructor.
    public UserLocations(String name, int lat, int lng, int rad, int ring, int medi, int noti, int syst) {

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
        locationAddress = "Latitude: " + Integer.toString(getLocationLat()) + " Longitude: "
                + Integer.toString(getLocationLng()) + " Radius: "
                + Integer.toString(getLocationRad());
        return locationAddress;
    }

    public int getLocationLat() {
        return locationLat;
    }

    public int getLocationLng() {
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
