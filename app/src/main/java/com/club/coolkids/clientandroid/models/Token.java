package com.club.coolkids.clientandroid.models;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.UUID;

public class Token {

    public Token(String tokenValue, String name){
        access_token = tokenValue;
        name_user = name;
    }

    public static Token token;

    private String access_token;

    private String name_user;

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

    public String getName() {
        return Token.token.name_user;
    }

    public void setName(String s) {
        Token.token.name_user = s;
    }
    public void deleteToken()
    {
        Token.token = null;
    }
}
