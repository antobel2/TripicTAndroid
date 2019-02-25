package com.club.coolkids.clientandroid.create_trip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.events.EventGetLocation;
import com.club.coolkids.clientandroid.models.NewBus;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        String apiKey = "AIzaSyDrNv1urfbos48QR2TFJRo7yhKrIfC9k9M";

        if (apiKey.equals("")) {
            Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
            return;
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
            PlacesClient placesClient = Places.createClient(this);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                double la = place.getLatLng().latitude;
                double lo = place.getLatLng().longitude;
                NewBus.bus.post(new EventGetLocation(la, lo));
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

//    private void setLaunchActivityClickListener(
//            int onClickResId, Class<? extends AppCompatActivity> activityClassToLaunch) {
//        findViewById(onClickResId)
//                .setOnClickListener(
//                        v -> startActivity(new Intent(MainActivity.this, activityClassToLaunch)));
//    }

    }
}
