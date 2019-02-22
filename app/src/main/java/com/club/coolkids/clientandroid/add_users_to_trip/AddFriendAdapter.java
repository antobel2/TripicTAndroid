package com.club.coolkids.clientandroid.add_users_to_trip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.dtos.SignedInUserDTO;
import com.club.coolkids.clientandroid.events.EventDeleteFriend;
import com.club.coolkids.clientandroid.models.NewBus;

public class AddFriendAdapter extends ArrayAdapter<SignedInUserDTO> {

    public AddFriendAdapter(@NonNull Context context) {
        super(context, R.layout.add_friends_list_item);
        NewBus.bus.register(this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(this.getContext());
        View v = li.inflate(R.layout.add_friends_list_item, null, false);
        SignedInUserDTO userDTO = getItem(position);
        final SignedInUserDTO u = userDTO;
        TextView tvUser = v.findViewById(R.id.tvUserAdd);
        Button fabDelete = v.findViewById(R.id.fabDelete);

        tvUser.setText(userDTO.lastName + ", " + userDTO.firstName + " | " + userDTO.userName);

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewBus.bus.post(new EventDeleteFriend(u));
            }
        });

        return v;
    }

}
