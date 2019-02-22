package com.club.coolkids.clientandroid.models.dtos;

public class CommentDTO {
    public String text;
    public String date;
    public String name;

    public CommentDTO(String text, String date, String name) {
        this.text = text;
        this.date = date;
        this.name = name;
    }
}
