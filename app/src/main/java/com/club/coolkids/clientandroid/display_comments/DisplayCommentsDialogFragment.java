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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.events.EventNewComment;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.models.dtos.CommentDTO;
import com.club.coolkids.clientandroid.models.dtos.CreateCommentDTO;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;
import com.squareup.otto.Subscribe;

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

        final EditText etvComment = v.findViewById(R.id.etvComment);
        final Button btSubmit = v.findViewById(R.id.btSubmit);
        disableButton(btSubmit);
        etvComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    enableButton(btSubmit);
                } else if (s.toString().trim().length() <= 0){
                        disableButton(btSubmit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressD = ProgressDialog.show(getContext(), getString(R.string.PleaseWait),
                        getString(R.string.ServerAnswer), true);
                final String textComment = etvComment.getText().toString();
                serverService.createComment("Bearer " + Token.token.getToken(), new CreateCommentDTO(textComment, getActivity().getIntent().getExtras().getInt("id"))).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), R.string.CommentCreated, Toast.LENGTH_SHORT).show();
                            NewBus.bus.post(new EventNewComment());
                            progressD.dismiss();
                        }
                        else {
                            Log.i("Retrofit", "code " + response.code());
                            progressD.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.i("Retrofit", "code " + t.getMessage());
                        Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                        progressD.dismiss();
                    }
                });
            }
        });

        //Rempli la liste de commentaires
        serverService.getCommentsByPostId("Bearer " + Token.token.getToken(), getActivity().getIntent().getExtras().getInt("id")).enqueue(new Callback<List<CommentDTO>>() {
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
                }
            }

            @Override
            public void onFailure(Call<List<CommentDTO>> call, Throwable t) {
                Log.i("Retrofit", "code " + t.getMessage());
                Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }

    private void enableButton(Button btn){
        btn.setEnabled(true);
        btn.setTextColor(getResources().getColor(R.color.colorTextDisabled));
        btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void disableButton(Button btn){
        btn.setEnabled(false);
        btn.setTextColor(getResources().getColor(R.color.colorTextDisabled));
        btn.setBackgroundColor(getResources().getColor(R.color.lightGray));
    }

    @Override
    public void onPause() {
        NewBus.bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        NewBus.bus.register(this);
        super.onResume();
    }

    @Subscribe
    public void newActivityEvent(EventNewComment e){
        dismiss();
    }
}
