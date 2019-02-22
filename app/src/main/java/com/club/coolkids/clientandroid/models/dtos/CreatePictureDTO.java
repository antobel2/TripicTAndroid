package com.club.coolkids.clientandroid.models.dtos;

public class CreatePictureDTO {

    public int postID;

    public String base64;

    public CreatePictureDTO(int pID, String b){
        postID = pID;
        base64 = b;
    }
}
