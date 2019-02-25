package com.club.coolkids.clientandroid.models.dtos;

public class CreateCommentDTO {

    public String text;
    public int postId;

    public CreateCommentDTO(String t, int id) {
        text = t;
        postId = id;
    }
}
