package com.club.coolkids.clientandroid.models.dtos;

public class UserSearchResultDTO {

    public String userId, firstName, lastName, userName;

    public UserSearchResultDTO(String i, String f, String l, String u){
        userId = i;
        firstName = f;
        lastName = l;
        userName = u;
    }
    @Override
    public String toString() {
        return lastName + ", " + firstName + " | " + userName;
    }
}
