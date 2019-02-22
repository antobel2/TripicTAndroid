package com.club.coolkids.clientandroid.models.dtos;

public class PictureDTO {
     public int id;
     public Byte[] content;

     public PictureDTO(int id, Byte[] content){
        this.id = id;
        this.content = content;
     }
}
