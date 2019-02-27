package com.club.coolkids.clientandroid.display_trips;

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
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.club.coolkids.clientandroid.display_activities.DisplayActivities;
import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.events.EventGetTrips;
import com.club.coolkids.clientandroid.login_signup.LoginActivity;
import com.club.coolkids.clientandroid.create_trip.TripDialogFragment;
import com.club.coolkids.clientandroid.models.dtos.SignedInUserDTO;
import com.club.coolkids.clientandroid.models.dtos.TripDTO;
import com.club.coolkids.clientandroid.events.EventNewTrip;
import com.club.coolkids.clientandroid.events.EventTripToActivities;
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

public class DisplayTrips extends AppCompatActivity {

    DisplayTripAdapter _adapter;
    DisplayNewTripAdapter _newAdapter;
    ListView _lvTrips;
    ListView _lvNewTrips;
    IDataService serverService;
    List<TripDTO> trips = new ArrayList<>();
    List<TripDTO> newTrips = new ArrayList<>();
    ActionBarDrawerToggle toggle;
    TextView _tvNoTrip;
    TextView _tvTrips;
    TextView _tvNewTrips;
    FloatingActionButton fab;
    private ProgressDialog progressD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_trips);
        setTitle(R.string.MyTrips);

        _lvTrips = findViewById(R.id.lvTrips);
        _tvNoTrip = findViewById(R.id.tvNoTrip);

        _adapter = new DisplayTripAdapter(this);
        _adapter.addAll(trips);
        _lvTrips.setAdapter(_adapter);

        _lvNewTrips = findViewById(R.id.lvNewTrips);
        // _tvNewNoTrip = findViewById(R.id.tvNewNoTrip);

        _newAdapter = new DisplayNewTripAdapter(this);
        _newAdapter.addAll(newTrips);
        _lvNewTrips.setAdapter(_newAdapter);

        _tvTrips = findViewById(R.id.tvTrips);
        _tvNewTrips = findViewById(R.id.tvNewTrips);

        serverService = DataService.getInstance().service;

        progressD = ProgressDialog.show(DisplayTrips.this, getString(R.string.pleaseWait),
                getString(R.string.ServerAnswer), true);

        serverService.getCurrentUser("Bearer " + Token.token.getToken()).enqueue(new Callback<SignedInUserDTO>() {
            @Override
            public void onResponse(Call<SignedInUserDTO> call, Response<SignedInUserDTO> response) {
                if (response.isSuccessful()) {
                    Token.token.setName(response.body().firstName + " " + response.body().lastName);
                    NavigationView navView = findViewById(R.id.nav_view);
                    final DrawerLayout drawer_layout = findViewById(R.id.drawer_layout);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    View header = navView.getHeaderView(0);
                    TextView nav_user = header.findViewById(R.id.userName);
                    nav_user.setText(getString(R.string.hello) + Token.token.getName());
                }
                else {
                    Token.token.setName("Guest");
                }
            }

            @Override
            public void onFailure(Call<SignedInUserDTO> call, Throwable t) {
                Log.i("Retrofit", "code " + t.getMessage());
                Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
            }
        });

        getTrips();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefreshTrips);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressD = ProgressDialog.show(DisplayTrips.this, getString(R.string.pleaseWait),
                        getString(R.string.ServerAnswer), true);
                startActivity(getIntent());
                pullToRefresh.setRefreshing(false);
            }
        });

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
                if (item.getItemId() == R.id.menu_displayTrips) {
                    Intent i = new Intent(getApplicationContext(), DisplayTrips.class);
                    finish();
                    startActivity(i);
                } else if (item.getItemId() == R.id.menu_logout) {
                    serverService.logout("Bearer " + Token.token.getToken()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Token.token.deleteToken();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                finish();
                                startActivity(i);
                            } else {
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
    }

    //Appel API
    public void getTrips() {
        serverService.getTrips("Bearer " + Token.token.getToken()).enqueue(new Callback<List<TripDTO>>() {
            @Override
            public void onResponse(Call<List<TripDTO>> call, Response<List<TripDTO>> response) {
                if (response.isSuccessful()) {
                    progressD.dismiss();
                    List<TripDTO> lstTrips = response.body();
                    if (lstTrips == null || lstTrips.size() == 0) {
                        _tvNoTrip.setVisibility(View.VISIBLE);
                        _lvTrips.setVisibility(View.GONE);
                        _lvNewTrips.setVisibility(View.GONE);
                    } else {
                        _tvNoTrip.setVisibility(View.GONE);
                        _lvNewTrips.setVisibility(View.VISIBLE);
                        _lvTrips.setVisibility(View.VISIBLE);
                        for (TripDTO t : lstTrips) {
                            if (!t.seen) {
                                _tvNewTrips.setVisibility(View.VISIBLE);
                                newTrips.add(t);
                            } else {
                                _tvTrips.setVisibility(View.VISIBLE);
                                trips.add(t);
                            }
                        }
                    }
                    _newAdapter.addAll(newTrips);
                    _lvNewTrips.setAdapter(_newAdapter);
                    _adapter.addAll(trips);
                    _lvTrips.setAdapter(_adapter);


                    ListUtils.setDynamicHeight(_lvNewTrips);
                    ListUtils.setDynamicHeight(_lvTrips);
                } else {
                    Log.i("Retrofit", "code " + response.code());
                    Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                    progressD.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<TripDTO>> call, Throwable t) {
                Log.i("Retrofit", "code " + t.getMessage());
                Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                progressD.dismiss();
            }
        });
    }

    //Drawer//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditDialog() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        TripDialogFragment tripDm = TripDialogFragment.newInstance(String.valueOf(R.string.createTrip));
        tripDm.show(fm, "Create TripDTO Dialog Fragment");
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
    public void getTripsEvent(EventGetTrips e) {
        finish();
        startActivity(getIntent());
    }

    @Subscribe
    public void newTripEvent(EventNewTrip e) {
        finish();
        startActivity(getIntent());
    }

    @Subscribe
    public void getActivitiesEvent(EventTripToActivities e) {
        Intent i = new Intent(getApplicationContext(), DisplayActivities.class);
        i.putExtra("TripName", e.tripDTO.name);
        i.putExtra("TripId", e.tripDTO.id);
        startActivity(i);
    }


    // Utilitaire pour ajuster la taille des listes
    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}
