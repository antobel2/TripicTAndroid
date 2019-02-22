package com.club.coolkids.clientandroid.models;

import android.support.v4.media.AudioAttributesCompat;

import okhttp3.MediaType;

public class LogInfo {

    public String username;
    public String password;
    public String grant_type;
    public MediaType content_type;
    public String lastname;
    public String firstname;

    public LogInfo(String u, String p){
        username = u;
        password = p;
    }

    public LogInfo(String u, String p, String f, String l){
        username = u;
        password = p;
        lastname = l;
        firstname = f;
    }
}