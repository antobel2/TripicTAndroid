package com.club.coolkids.clientandroid.display_trips;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.dtos.TripDTO;
import com.club.coolkids.clientandroid.events.EventTripToActivities;
import com.club.coolkids.clientandroid.models.NewBus;

public class DisplayNewTripAdapter extends ArrayAdapter<TripDTO> {

    public DisplayNewTripAdapter(@NonNull Context context) {
        super(context, R.layout.display_trip_new_list_item);
        NewBus.bus.register(this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View v = li.inflate(R.layout.display_trip_new_list_item, null, false);
        final TripDTO tripDTO = getItem(position);
        TextView tvTrip = v.findViewById(R.id.tvNewTrip);

        tvTrip.setText(tripDTO.name);

        tvTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewBus.bus.post(new EventTripToActivities(tripDTO));
            }
        });

        return v;
    }
}
