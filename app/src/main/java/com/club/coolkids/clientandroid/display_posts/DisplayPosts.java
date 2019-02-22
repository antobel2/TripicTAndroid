package com.club.coolkids.clientandroid.display_posts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.club.coolkids.clientandroid.create_post.CreatePost;
import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.display_trips.DisplayTrips;
import com.club.coolkids.clientandroid.login_signup.LoginActivity;
import com.club.coolkids.clientandroid.display_post_details.PostDetails;
import com.club.coolkids.clientandroid.models.dtos.PostDTO;
import com.club.coolkids.clientandroid.events.EventOnClickForDetails;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayPosts extends AppCompatActivity {

    private IDataService serverService;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context cntxt;
    private ProgressDialog progressD;
    ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_posts);
        cntxt = this.getBaseContext();
        setTitle(this.getIntent().getStringExtra("ActivityName"));

        ///// DRAWER /////
        NavigationView navView = findViewById(R.id.nav_view);
        final DrawerLayout drawer_layout = findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View header = navView.getHeaderView(0);
        TextView nav_user = header.findViewById(R.id.userName);
        nav_user.setText(getIntent().getStringExtra("Username"));
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

        serverService = DataService.getInstance().service;

        // show progressBar
        progressD = ProgressDialog.show(DisplayPosts.this, "Veuillez patienter",
                "Attente de réponse du serveur", true);


        getPostsFromServer();

        // FAB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = getIntent().getIntExtra("ActivityId", 0);
                Intent i = new Intent(getApplicationContext(), CreatePost.class);
                i.putExtra("ActivityId", id);
                startActivity(i);
            }
        });

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressD = ProgressDialog.show(DisplayPosts.this, "Veuillez patienter",
                        "Attente de réponse du serveur", true);
                getPostsFromServer();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    //TODO for activity only
    public void getPostsFromServer(){
        serverService.getPostForActivity("Bearer " + Token.token.getToken(), this.getIntent().getIntExtra("ActivityId", 0)).enqueue(new Callback<List<PostDTO>>() {
            @Override
            public void onResponse(Call<List<PostDTO>> call, Response<List<PostDTO>> response) {
                if (response.isSuccessful()){
                    progressD.dismiss();
                    List<PostDTO> postsFromBody = response.body();

                    // changer laffichage s'il n'y a aucun post
                    if (postsFromBody.size() == 0){
                        TextView txtNoPost = findViewById(R.id.txtViewNoPosts);
                        txtNoPost.setVisibility(View.VISIBLE);
                        RecyclerView recycler = recycler = findViewById(R.id.recyclerView);
                        recycler.setVisibility(View.GONE);
                    }


                    //changer en [] pour l'adapteur
                    PostDTO[] contentsForDataSet = new PostDTO[postsFromBody.size()];
                    int index = 0;
                    for (PostDTO p : postsFromBody){
                        contentsForDataSet[index] = p;
                        index++;
                    }

                    // Cree le Recycler
                    recycler = findViewById(R.id.recyclerView);
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    recycler.setHasFixedSize(true);

                    // use a linear layout manager
                    layoutManager = new LinearLayoutManager(cntxt);
                    recycler.setLayoutManager(layoutManager);

                    // specify an adapter
                    adapter = new DisplayPostAdapter(cntxt, contentsForDataSet,new DisplayPostAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(PostDTO item) {
                        }
                    });

                    recycler.setAdapter(adapter);
                }
                else{
                    Log.i("Retrofit", "code " + response.code());
                    Toast.makeText(cntxt, R.string.errConectServer, Toast.LENGTH_SHORT).show();
                    progressD.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<PostDTO>> call, Throwable t) {
                Log.i("Retrofit", "code " + t.getMessage());
                progressD.dismiss();
                Toast.makeText(cntxt, R.string.errConectServer, Toast.LENGTH_SHORT).show();
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


    //Bus
    @Override
    protected void onPause() {
        NewBus.bus.unregister(this);
        super.onPause();
    }

    //Bus
    @Override
    protected void onResume() {
        NewBus.bus.register(this);
        super.onResume();
    }

    @Subscribe public void displayPostDetail(EventOnClickForDetails e){
        Intent intent = new Intent(this, PostDetails.class);
        intent.putExtra("id", e.id);
        intent.putStringArrayListExtra("idTable", e.idTable);
        intent.putExtra("text", e.text);
        intent.putExtra("date", e.date);
        intent.putExtra("userName", e.userName);
        startActivity(intent);
    }
}
