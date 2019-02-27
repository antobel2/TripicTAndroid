package com.club.coolkids.clientandroid.events;

public class EventGetLocation {

    public double latitude;
    public double longitude;
    public String name;

    public EventGetLocation(double la, double lo, String na) {
        latitude = la;
        longitude = lo;
        name = na;
    }
}
