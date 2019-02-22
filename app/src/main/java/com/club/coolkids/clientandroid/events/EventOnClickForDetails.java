package com.club.coolkids.clientandroid.events;

import java.util.ArrayList;

public class EventOnClickForDetails {

    public int id;
    public String text;
    public ArrayList<String> idTable;
    public String date;
    public String userName;

    public EventOnClickForDetails(int id, String text, ArrayList<String> idTable, String date, String u){
        this.id = id;
        this.text = text;
        this.idTable = idTable;
        this.date = date;
        userName = u;
    }
}
