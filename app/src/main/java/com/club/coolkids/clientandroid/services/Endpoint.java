package com.club.coolkids.clientandroid.services;

public class Endpoint {

    public Endpoint(){
        end_point = local;
    }

    public static Endpoint endpoint;

    private String end_point;

    private String test = "http://e1-test.projet.college-em.info:8080";
    private String dev = "http://e1-dev.projet.college-em.info:8080";
    private String local = "http://10.0.2.2:52090";
}
