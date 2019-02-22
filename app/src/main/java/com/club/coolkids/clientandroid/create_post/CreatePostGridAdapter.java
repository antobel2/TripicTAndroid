package com.club.coolkids.clientandroid.create_post;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.ImageUploadElement;

import java.util.List;


public class CreatePostGridAdapter extends RecyclerView.Adapter<CreatePostGridAdapter.MyViewHolder> {

    Context context;
    public List <ImageUploadElement> dataSet;

    public CreatePostGridAdapter(Context context, List<ImageUploadElement> p_images) {
        this.context = context;
        dataSet = p_images;
    }

    @Override
    public CreatePostGridAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.image_to_upload, parent, false); // pass the view to View Holder
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CreatePostGridAdapter.MyViewHolder holder, int position) {

        RequestOptions options = new RequestOptions()
                .placeholderOf(android.R.drawable.btn_minus)
                .centerCrop();
        ImageUploadElement elt = this.dataSet.get(position);
        Uri url = elt.uri;
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(holder.image);
        Log.i("understandingUIbug", "adapter working  for  " + elt.uri + " "+elt.progressState);
        setProgress(elt.progressState, holder.image, holder.progress);

    }

    @Override
    public int getItemCount() {
        return dataSet.toArray().length;
    }

    //initialises la vue de du ImagePreview contenu dans le recycler
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView image;
        public ProgressBar progress;

        public MyViewHolder(FrameLayout itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageUploadPreview);
            progress = itemView.findViewById(R.id.img_progress);
        }
    }

    public void setProgress(EnuProgress progressState, SquareImageView image, ProgressBar progress) {

        switch (progressState){
            //mets l'image en gris pour montrer qu'elle est en attente
            case waiting:
                setActiveGrey(image, true);
                break;
            //mets un spinner par-dessus l'image pour montrer qu'elle est en envoi
            case ongoing:
                showProgress(progress,true);
                break;
            //l'image devient en couleur pour prouver qu'elle a été acceptée
            case complete:
                showProgress(progress,false);
                setActiveGrey(image,false);
                break;
            //l'image va avoir une option pour l'enlever de la sélaction ou la réenvoyer..? à voir
            case failed:
                break;
        }

    }

    //mets un filtre avec saturation 0, qui rends l'image en noir et blanc
    private void setActiveGrey(SquareImageView image, Boolean active){
        //mets le filtre
        if(active){
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);  //0 means grayscale
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            image.setColorFilter(cf);
        }
        //enleve le filtre
        else{
            image.setColorFilter(null);
        }
    }

    //tout dépendant de shorProgress ou non, dévoile ou cache le progress spinner
    private void showProgress(ProgressBar progress, final boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
