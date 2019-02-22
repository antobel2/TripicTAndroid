package com.club.coolkids.clientandroid.services;

public final class LoggedUser {
    public String id;
    public String name;

    private static LoggedUser instance = new LoggedUser();

    public final static LoggedUser getInstance() {
        return instance;
    }

    public void signout(){
        name = "";
        id = "";
    }
}
