package com.club.coolkids.clientandroid.events;

public class EventNewActivity {

    public String activity;
    public int tripId;

    public EventNewActivity(String eActivity, int tripId){
        this.activity = eActivity;
        this.tripId = tripId;
    }
}
