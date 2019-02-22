package com.club.coolkids.clientandroid.models.dtos;

import java.util.ArrayList;
import java.util.List;

public class PostDTO {

    public int id;
    public String text;
    public List<Integer> idTable;
    public String date;
    public String userName;

    public PostDTO(int i, String t, List<Integer> idTable, String u){
        this.idTable = idTable;
        userName = u;
    }
}
