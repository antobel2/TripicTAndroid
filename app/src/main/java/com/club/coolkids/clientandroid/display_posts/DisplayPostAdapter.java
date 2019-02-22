package com.club.coolkids.clientandroid.display_posts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.dtos.PostDTO;
import com.club.coolkids.clientandroid.events.EventOnClickForDetails;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.services.IDataService;

import java.util.ArrayList;
import java.util.List;

public class DisplayPostAdapter extends RecyclerView.Adapter<DisplayPostAdapter.MyViewHolder> {
    private PostDTO[] mDataset;
    public PostDTO mCurrentPost;
    Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PostDTO item);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView postDescriptionTextView;
        public TextView postDateTextView;
        public TextView textNumberOfPics;
        public ImageView imageView;
        public TextView textUsername;
        public List<String> idTableForPost;

        public MyViewHolder(LinearLayout v) {
            super(v);
            postDescriptionTextView = v.findViewById(R.id.tvdisplayPost_description);
            postDateTextView = v.findViewById(R.id.tvDisplayPost_Date);
            textNumberOfPics = v.findViewById(R.id.imagePreviewTextNumber);
            imageView = v.findViewById(R.id.imagePreviewPost);
            textUsername = v.findViewById(R.id.tvDisplayPost_Username);
        }

        // MEthode qui bind les bon items du recycler view pour le onclicklistener pour envoyer a l'activite details avec le bon post
        public void bind(final PostDTO item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                    ArrayList<String> toArrayList = new ArrayList<>();
                    for (int i : item.idTable){
                        toArrayList.add(Integer.toString(i));
                    }
                    NewBus.bus.post(new EventOnClickForDetails(item.id, item.text, toArrayList, item.date, item.name));
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DisplayPostAdapter(Context context, PostDTO[] myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        this.context = context;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DisplayPostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_post_recycler_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bind(mDataset[position], listener);
        // - get element from your dataset at this position
        mCurrentPost = mDataset[position];

        //Doit remettre la vue a zero sinon le recycler pourrait recycler une vielle vue et mettre des vielles infos si la nouvelle vue n'a pas de nouveau data a mettre.
        holder.postDescriptionTextView.setText("");
        holder.postDateTextView.setText("");
        holder.textNumberOfPics.setVisibility(View.INVISIBLE);
        holder.textUsername.setText("");
        Glide.with(holder.postDateTextView.getContext()).clear(holder.imageView);

        //Pas de photo dans le post, afficher l'image pas de photo
        if (mCurrentPost.idTable.size() == 0){
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(holder.postDateTextView.getContext())
                    .asBitmap()
                    .load(R.drawable.no_image_available2)
                    .into(holder.imageView);
        }

        //Seulement une photo dans le post, ne pas afficher le textview indiquant le reste de photo
        if (mCurrentPost.idTable.size() > 0){
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(holder.postDateTextView.getContext())
                    .asBitmap()
                    .load(IDataService.endpoint + "/api/Pictures/GetPictureFromId/" + mCurrentPost.idTable.get(0)) //toujours prendre la premiere image
                    .into(holder.imageView);
        }

        //Afficher le nombre de photo restante
        if (mCurrentPost.idTable.size() > 1){
            holder.textNumberOfPics.setText("+" + (mCurrentPost.idTable.size() - 1));
            holder.textNumberOfPics.setVisibility(View.VISIBLE);
        }

        holder.postDescriptionTextView.setText(mDataset[position].text);
        holder.postDateTextView.setText(mDataset[position].date);
        holder.textUsername.setText(mDataset[position].name);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}