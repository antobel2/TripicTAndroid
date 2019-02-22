package com.club.coolkids.clientandroid.create_post.quilt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.Images;


import java.util.ArrayList;
import java.util.List;

/**
 * tout pris sur https://stackoverflow.com/questions/17716325/show-images-from-gallery-in-mosaic-style-in-android
 */

public class QuiltActivity extends AppCompatActivity {

    QuiltView quiltView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quilt);
        this.quiltView = (QuiltView) findViewById(R.id.quilt);
        addTestQuilts(200);
    }

    public void addTestQuilts(int num){
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        List<ImageView> images = new ArrayList<>();
        for (String url : Images.imageUrls) {
            ImageView iv = new ImageView(this.getApplicationContext());
            Glide.with(this).load(url).apply(options).into(iv);
            images.add(iv);
        }
        quiltView.addPatchImages(images);
    }
}
