package com.club.coolkids.clientandroid.events;

import com.club.coolkids.clientandroid.models.dtos.SignedInUserDTO;

public class EventDeleteFriend {
    public SignedInUserDTO userDTO;

    public EventDeleteFriend(SignedInUserDTO u) {
        userDTO = u;
    }
}
