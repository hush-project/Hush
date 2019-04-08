package com.hushproject.hush;

public class UserLocations {

    private String locationName = "";
    private String locationAddress = "";
    private int locRingVol = 0;
    private int locMediVol = 0;
    private int locNotiVol = 0;
    private int locSystVol = 0;

    //constructor.
    public UserLocations(String name, String address, int ring, int medi, int noti, int syst) {

       locationName = name;
       locationAddress = address;
       locRingVol = ring;
       locMediVol = medi;
       locNotiVol = noti;
       locSystVol = syst;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
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
