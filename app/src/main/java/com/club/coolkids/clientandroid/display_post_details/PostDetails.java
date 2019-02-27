package com.club.coolkids.clientandroid.display_post_details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.add_users_to_trip.AddFriendsDialogFragment;
import com.club.coolkids.clientandroid.display_comments.DisplayCommentsDialogFragment;
import com.club.coolkids.clientandroid.events.EventNewComment;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.IDataService;
import com.squareup.otto.Subscribe;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

public class PostDetails extends AppCompatActivity {

    ArrayList<String> postIdTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_post_details);

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

        FrameLayout frameLayout = findViewById(R.id.frameCarousel);
        frameLayout.setVisibility(View.VISIBLE);

        if (postIdTable.size() == 0){
            frameLayout.setVisibility(View.GONE);
            carouselView.setBackgroundResource(R.drawable.no_image_available2);
        }

        carouselView.setImageListener(imageListener);
        TextView tvDisplayPost_Date = findViewById(R.id.tvDisplayPost_Date);
        tvDisplayPost_Date.setText(postDate);
        TextView tvdisplayPost_description = findViewById(R.id.tvdisplayPost_description);
        TextView tvUsername = findViewById(R.id.tvDisplayPost_Username);
        tvUsername.setText(Token.token.getName());
        tvdisplayPost_description.setText(postText);
        tvdisplayPost_description.setVisibility(View.VISIBLE);

        if (tvdisplayPost_description.getText().toString().isEmpty() || tvdisplayPost_description.getText().toString() == null || tvdisplayPost_description.getText().toString() == "") {
            tvdisplayPost_description.setVisibility(View.GONE);
        }

        Button link_comments = findViewById(R.id.link_comments);
        link_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialogDisplayComments();
            }
        });
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
                        .load(IDataService.endpoint + "/api/Pictures/GetPictureFromId/" + postIdTable.get(position))
                        .into(imageView);
            }
        }
    };

    private void showEditDialogDisplayComments(){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        DisplayCommentsDialogFragment displayCommentsDm = DisplayCommentsDialogFragment.newInstance(String.valueOf(R.string.comments), this.getIntent().getIntExtra("PostId", 0));
        displayCommentsDm.show(fm, "Display Comments Dialog Fragment");
    }

    @Override
    protected void onPause() {
        NewBus.bus.unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        NewBus.bus.register(this);
        super.onResume();
    }

    @Subscribe
    public void newActivityEvent(EventNewComment e){
        showEditDialogDisplayComments();
    }
}
