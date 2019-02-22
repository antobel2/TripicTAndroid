package com.club.coolkids.clientandroid.models.dtos;

public class SignedInUserDTO {

    public String uuid, userName, firstName, lastName;

    public SignedInUserDTO(String uid, String u, String f, String l){
        uuid = uid;
        userName = u;
        firstName = f;
        lastName = l;
    }
}
