package com.club.coolkids.clientandroid.models.dtos;

public class UserSearchResultDTO {

    public String userId, firstName, lastName, userName;

    public UserSearchResultDTO(String f, String l, String u){
        firstName = f;
        lastName = l;
        userName = u;
    }
    @Override
    public String toString() {
        return lastName + ", " + firstName + " | " + userName;
    }
}
