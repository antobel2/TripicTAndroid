package com.club.coolkids.clientandroid.display_activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.dtos.ActivityDTO;
import com.club.coolkids.clientandroid.events.EventActivityToPosts;
import com.club.coolkids.clientandroid.models.NewBus;

public class DisplayActivityAdapter extends ArrayAdapter<ActivityDTO> {

    public DisplayActivityAdapter(@NonNull Context context) {
        super(context, R.layout.display_activities_list_item);
        NewBus.bus.register(this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View v = li.inflate(R.layout.display_activities_list_item, null, false);
        final ActivityDTO activityDTO = getItem(position);
        TextView tvActivity = v.findViewById(R.id.tvActivity);

        tvActivity.setText(activityDTO.name);

        tvActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewBus.bus.post(new EventActivityToPosts(activityDTO));
            }
        });

        return v;
    }
}
