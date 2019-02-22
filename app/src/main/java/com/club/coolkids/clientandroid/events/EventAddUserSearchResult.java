package com.club.coolkids.clientandroid.events;

import com.club.coolkids.clientandroid.models.dtos.UserSearchResultDTO;

public class EventAddUserSearchResult {

    public UserSearchResultDTO userDTO;

    public EventAddUserSearchResult(UserSearchResultDTO u) {
        userDTO = u;
    }
}
