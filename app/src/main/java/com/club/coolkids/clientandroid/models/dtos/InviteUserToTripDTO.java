package com.club.coolkids.clientandroid.models.dtos;

import java.util.List;

public class InviteUserToTripDTO {

    public List<String> userIds;
    public int tripId;

    public InviteUserToTripDTO(int id){
        tripId = id;
    }

}
