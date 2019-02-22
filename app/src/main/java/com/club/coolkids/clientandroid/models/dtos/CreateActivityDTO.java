package com.club.coolkids.clientandroid.models.dtos;

public class CreateActivityDTO {

    public String name;
    public int tripID;

    public CreateActivityDTO(String n, int t){
        name = n;
        tripID = t;
    }
}
