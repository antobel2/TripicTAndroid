package com.club.coolkids.clientandroid.display_comments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.models.dtos.CommentDTO;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayCommentsDialogFragment extends DialogFragment {

    IDataService serverService;
    private ProgressDialog progressD;
    ListView lvComments;
    DisplayCommentsAdapter adapter;
    List<CommentDTO> comments = new ArrayList<>();

    public DisplayCommentsDialogFragment(){}

    public static DisplayCommentsDialogFragment newInstance(String title, int postId){
        DisplayCommentsDialogFragment fragment = new DisplayCommentsDialogFragment();
        Bundle args = new Bundle();
        args.putString(String.valueOf(R.string.comments), title);
        args.putInt("PostId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.comments);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = inflater.inflate(R.layout.display_comments_dialog, null);
        builder.setView(v);

        lvComments = v.findViewById(R.id.lvComments);

        adapter = new DisplayCommentsAdapter(getContext());
        adapter.addAll(comments);
        lvComments.setAdapter(adapter);

        serverService = DataService.getInstance().service;
        //Rempli la liste de commentaires
        serverService.getCommentsByPostId("Bearer" + Token.token.getToken(), getActivity().getIntent().getIntExtra("PostId", 0)).enqueue(new Callback<List<CommentDTO>>() {
            @Override
            public void onResponse(Call<List<CommentDTO>> call, Response<List<CommentDTO>> response) {
                if (response.isSuccessful()) {
                    List<CommentDTO> lstComments = response.body();
                    for (CommentDTO c:lstComments) {
                        comments.add(c);
                    }
                    adapter.addAll(comments);
                    lvComments.setAdapter(adapter);
                }
                else {
                    Log.i("Retrofit", "code " + response.code());
                    Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                    progressD.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<CommentDTO>> call, Throwable t) {
                Log.i("Retrofit", "code " + t.getMessage());
                Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                progressD.dismiss();
            }
        });
        return builder.create();
    }
}
