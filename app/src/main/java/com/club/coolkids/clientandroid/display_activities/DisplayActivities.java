package com.club.coolkids.clientandroid.display_activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.club.coolkids.clientandroid.add_users_to_trip.AddFriendsDialogFragment;
import com.club.coolkids.clientandroid.create_activitiy.ActivityDialogFragment;
import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.display_posts.DisplayPosts;
import com.club.coolkids.clientandroid.display_trips.DisplayTrips;
import com.club.coolkids.clientandroid.see_users_in_trip.FriendsInTripDialogFragment;
import com.club.coolkids.clientandroid.login_signup.LoginActivity;
import com.club.coolkids.clientandroid.models.dtos.ActivityDTO;
import com.club.coolkids.clientandroid.events.EventActivityToPosts;
import com.club.coolkids.clientandroid.events.EventNewActivity;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayActivities extends AppCompatActivity implements View.OnClickListener {

    DisplayActivityAdapter _adapter;
    ListView _lvActivities;
    IDataService serverService;
    List<ActivityDTO> activities = new ArrayList<>();
    ActionBarDrawerToggle toggle;
    TextView _tvNoActivity;
    private ProgressDialog progressD;
    private Boolean _isFabOpen = false;
    private FloatingActionButton fabBase,fabAddActivity,fabViewFriends,fabAddFriends;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activities);
        setTitle(this.getIntent().getStringExtra("TripName"));

        _lvActivities = findViewById(R.id.lvActivities);
        _tvNoActivity = findViewById(R.id.tvNoActivity);

        _adapter = new DisplayActivityAdapter(this);
        _adapter.addAll(activities);
        _lvActivities.setAdapter(_adapter);

        serverService = DataService.getInstance().service;
        getActivities();

        //FABs

        fabBase = findViewById(R.id.fabBase);
        fabAddActivity = findViewById(R.id.fabAddActivity);
        fabViewFriends = findViewById(R.id.fabViewFriends);
        fabAddFriends = findViewById(R.id.fabAddFriends);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        fabBase.setOnClickListener(this);
        fabAddActivity.setOnClickListener(this);
        fabViewFriends.setOnClickListener(this);
        fabAddFriends.setOnClickListener(this);

        ///// DRAWER /////
        NavigationView navView = findViewById(R.id.nav_view);
        final DrawerLayout drawer_layout = findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View header = navView.getHeaderView(0);
        TextView nav_user = header.findViewById(R.id.userName);
        nav_user.setText(getString(R.string.hello) + Token.token.getName());
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_displayTrips)
                {
                    Intent i = new Intent(getApplicationContext(), DisplayTrips.class);
                    startActivity(i);
                }
                else if(item.getItemId() == R.id.menu_logout)
                {
                    serverService.logout("Bearer " + Token.token.getToken()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful())
                            {
                                Token.token.deleteToken();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                finish();
                                startActivity(i);
                            }
                            else
                            {
                                Log.i("Retrofit", "code " + response.code());
                                Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                                progressD.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.i("Retrofit", "code " + t.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                            progressD.dismiss();
                        }
                    });

                }
                drawer_layout.closeDrawers();
                return false;
            }
        });

        toggle = new ActionBarDrawerToggle(
                this,
                drawer_layout,
                R.string.open_drawer,
                R.string.close_drawer);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressD = ProgressDialog.show(DisplayActivities.this, "Veuillez patienter",
                        "Attente de réponse du serveur", true);
                finish();
                startActivity(getIntent());
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    //Appel API
    public void getActivities(){
        serverService.getActivities("Bearer " + Token.token.getToken(), this.getIntent().getIntExtra("TripId", 0)).enqueue(new Callback<List<ActivityDTO>>() {
            @Override
            public void onResponse(Call<List<ActivityDTO>> call, Response<List<ActivityDTO>> response) {
                if (response.isSuccessful()){
                    List<ActivityDTO> lstActivities = response.body();
                    if (lstActivities == null || lstActivities.size() == 0){
                        _tvNoActivity.setVisibility(View.VISIBLE);
                        _lvActivities.setVisibility(View.GONE);
                    }
                    else{
                        _tvNoActivity.setVisibility(View.GONE);
                        _lvActivities.setVisibility(View.VISIBLE);
                        for (ActivityDTO a:lstActivities) {
                            activities.add(a);
                        }
                    }

                    _adapter.addAll(activities);
                    _lvActivities.setAdapter(_adapter);
                }
                else{
                    Log.i("Retrofit", "code " + response.code());
                    Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                    progressD.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<ActivityDTO>> call, Throwable t) {
                Log.i("Retrofit", "code " + t.getMessage());
                Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                progressD.dismiss();
            }
        });
    }

    //Drawer//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditDialogAddActivity(){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        ActivityDialogFragment activityDm = ActivityDialogFragment.newInstance(String.valueOf(R.string.createActivity), this.getIntent().getIntExtra("TripId", 0));
        activityDm.show(fm, "Create ActivityDTO Dialog Fragment");
    }

    private void showEditDialogSeeFriends(){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FriendsInTripDialogFragment friendsDm = FriendsInTripDialogFragment.newInstance(String.valueOf(R.string.friendsInTrip), this.getIntent().getIntExtra("TripId", 0));
        friendsDm.show(fm, "See Users Dialog Fragment");
    }

    private void showEditDialogAddFriends(){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        AddFriendsDialogFragment addFriendsDm = AddFriendsDialogFragment.newInstance(String.valueOf(R.string.addFriends), this.getIntent().getIntExtra("TripId", 0));
        addFriendsDm.show(fm, "Add Users Dialog Fragment");
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
    public void newActivityEvent(EventNewActivity e){
        finish();
        startActivity(getIntent());
    }

    @Subscribe
    public void getPostsEvent(EventActivityToPosts e){
        Intent i = new Intent(getApplicationContext(), DisplayPosts.class);
        i.putExtra("ActivityName", e.activityDTO.name);
        i.putExtra("ActivityId", e.activityDTO.id);
        startActivity(i);
    }


    //FABs
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fabBase:
                animateFAB();
                break;
            case R.id.fabAddActivity:
                Log.d("FAB", "fabAddActivity");
                showEditDialogAddActivity();
                break;
            case R.id.fabViewFriends:
                Log.d("FAB", "fabViewFriends");
                showEditDialogSeeFriends();
                break;
            case R.id.fabAddFriends:
                Log.d("FAB", "fabAddFriends");
                showEditDialogAddFriends();
                break;
        }
    }

    public void animateFAB(){

        if(_isFabOpen){

            fabBase.startAnimation(rotate_backward);
            fabAddActivity.startAnimation(fab_close);
            fabViewFriends.startAnimation(fab_close);
            fabAddFriends.startAnimation(fab_close);
            fabAddActivity.setClickable(false);
            fabViewFriends.setClickable(false);
            fabAddFriends.setClickable(false);
            _isFabOpen = false;
            Log.d("FAB", "close");

        } else {

            fabBase.startAnimation(rotate_forward);
            fabAddActivity.startAnimation(fab_open);
            fabViewFriends.startAnimation(fab_open);
            fabAddFriends.startAnimation(fab_open);
            fabAddActivity.setClickable(true);
            fabViewFriends.setClickable(true);
            fabAddFriends.setClickable(true);
            _isFabOpen = true;
            Log.d("FAB","open");

        }
    }
}
