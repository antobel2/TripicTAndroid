package com.club.coolkids.clientandroid.events;

import com.club.coolkids.clientandroid.models.dtos.UserSearchResultDTO;

public class EventGetUserSearchResult {

    public UserSearchResultDTO userDTO;

    public EventGetUserSearchResult(UserSearchResultDTO u) {
        userDTO = u;
    }

}
