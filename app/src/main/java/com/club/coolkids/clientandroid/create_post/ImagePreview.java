package com.club.coolkids.clientandroid.create_post;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.club.coolkids.clientandroid.R;

public class ImagePreview extends AppCompatActivity {

    public ProgressBar progress;
    public SquareImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_to_upload);
        image = findViewById(R.id.imageUploadPreview);
        progress = findViewById(R.id.img_progress);
    }

}
