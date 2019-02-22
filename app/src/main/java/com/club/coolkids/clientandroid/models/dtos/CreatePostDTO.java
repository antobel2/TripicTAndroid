package com.club.coolkids.clientandroid.models.dtos;

import java.util.ArrayList;

public class CreatePostDTO {

    public String text;
    public int picCount;
    public int activityId;


    public CreatePostDTO(int imgCount, String desc, int actId){
        picCount = imgCount;
        text = desc;
        activityId = actId;
    }

}
