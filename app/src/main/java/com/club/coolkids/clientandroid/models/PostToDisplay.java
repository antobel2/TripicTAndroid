package com.club.coolkids.clientandroid.models;

import android.graphics.Bitmap;

public class PostToDisplay {
    public int id;
    public String text;
    public Bitmap[] pictures;
    public String date;

    public PostToDisplay(int id, String text, String date, int nmbPictures){
        if (nmbPictures > 0){
            this.pictures = new Bitmap[nmbPictures];
        }
        this.id = id;
        if (text == null){
            this.text = "";
        }
        else{
            this.text = text;
        }
        this.date = date;
    }
}
