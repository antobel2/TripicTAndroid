package com.club.coolkids.clientandroid.models;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.UUID;

public class Token {

    public Token(String tokenValue){
        access_token = tokenValue;
    }

    public static Token token;

    private String access_token;

    @Override
    public String toString() {
        return "access_token";
    }

    public String getToken(){
        return access_token;
    }

    public void setToken(Token t)
    {
        Token.token = t;
    }

    public void deleteToken()
    {
        Token.token = null;
    }
}
