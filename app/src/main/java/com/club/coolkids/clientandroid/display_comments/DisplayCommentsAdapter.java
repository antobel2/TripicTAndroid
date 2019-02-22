package com.club.coolkids.clientandroid.display_comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.dtos.CommentDTO;

public class DisplayCommentsAdapter extends ArrayAdapter<CommentDTO> {

    public DisplayCommentsAdapter(@NonNull Context context) {
        super(context, R.layout.display_comments_list_item);
        NewBus.bus.register(this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View v = li.inflate(R.layout.display_comments_list_item, null, false);
        final CommentDTO commentDTO = getItem(position);
        TextView username = v.findViewById(R.id.userName);
        TextView date = v.findViewById(R.id.date);
        TextView comment = v.findViewById(R.id.tvComment);

        username.setText(commentDTO.name);
        date.setText(commentDTO.date);
        comment.setText(commentDTO.text);

        return v;
    }
}
