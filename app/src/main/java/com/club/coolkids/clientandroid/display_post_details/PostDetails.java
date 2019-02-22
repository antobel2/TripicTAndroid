package com.club.coolkids.clientandroid.display_post_details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.Token;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

public class PostDetails extends AppCompatActivity {

    ArrayList<String> postIdTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_post_details);

        NewBus.bus.register(this);


        //recuper id du post dans le intent
        Intent myIntent = getIntent();
        int postId = myIntent.getIntExtra("id", 0);
        String postText = myIntent.getStringExtra("text");
        postIdTable = myIntent.getStringArrayListExtra("idTable");
        String postDate = myIntent.getStringExtra("date");
        String userName = myIntent.getStringExtra("userName");


        // ajouter les elements dans le ui
        CarouselView carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(postIdTable.size());

        if (postIdTable.size() == 0){
            carouselView.setBackgroundResource(R.drawable.no_image_available2);
        }

        carouselView.setImageListener(imageListener);
        TextView tvDisplayPost_Date = findViewById(R.id.tvDisplayPost_Date);
        tvDisplayPost_Date.setText(postDate);
        TextView tvdisplayPost_description = findViewById(R.id.tvdisplayPost_description);
        TextView tvUsername = findViewById(R.id.tvDisplayPost_Username);
        tvUsername.setText(Token.token.getName());
        tvdisplayPost_description.setText(postText);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (postIdTable.size() == 0){
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(R.drawable.no_image_available2)
                        .into(imageView);
            }
            else{
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load("http://e1-dev.projet.college-em.info:8080/api/Pictures/GetPictureFromId/" + postIdTable.get(position))
                        .into(imageView);
            }
        }
    };

}
