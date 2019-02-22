package com.club.coolkids.clientandroid.see_users_in_trip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.dtos.SignedInUserDTO;
import com.club.coolkids.clientandroid.models.NewBus;

public class UserAdapter extends ArrayAdapter<SignedInUserDTO> {

    public UserAdapter(@NonNull Context context) {
        super(context, R.layout.see_friends_list_item);
        NewBus.bus.register(this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View v = li.inflate(R.layout.see_friends_list_item, null, false);
        final SignedInUserDTO userDTO = getItem(position);
        TextView tvUser = v.findViewById(R.id.tvUser);

        tvUser.setText(userDTO.lastName + ", " + userDTO.firstName + " | " + userDTO.userName);

//        tvUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NewBus.bus.post(new EventActivityToPosts(userDTO));
//            }
//        });

        return v;
    }
}
