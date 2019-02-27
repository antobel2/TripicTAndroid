package com.club.coolkids.clientandroid.models.dtos;

public class CreateTripDTO {

    public String name;
    public double latitude;
    public double longitude;

    public CreateTripDTO(String name){
        this.name = name;
        latitude = 0;
        longitude = 0;
    }

    public CreateTripDTO(String name, double lat, double lon){
        this.name = name;
        latitude = lat;
        longitude = lon;
    }
}
