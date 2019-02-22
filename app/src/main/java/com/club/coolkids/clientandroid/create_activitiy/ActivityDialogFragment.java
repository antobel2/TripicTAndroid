package com.club.coolkids.clientandroid.create_activitiy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.dtos.CreateActivityDTO;
import com.club.coolkids.clientandroid.events.EventNewActivity;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivityDialogFragment extends DialogFragment {

    EditText _nameText;
    Button _btnAdd;
    Button _btnCancel;
    IDataService serverService;
    TextInputLayout _inputLayout_addedActivityName;
    private ProgressDialog progressD;

    public ActivityDialogFragment(){}

    public static ActivityDialogFragment newInstance(String title, int tripId){
        ActivityDialogFragment fragment = new ActivityDialogFragment();
        Bundle args = new Bundle();
        args.putString(String.valueOf(R.string.createActivity), title);
        args.putInt("TripId", tripId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        _nameText = view.findViewById(R.id.input_addedItemName);
//        _btnAdd = view.findViewById(R.id.btn_add);
//        _btnCancel = view.findViewById(R.id.btn_cancel);
//
//        _nameText.requestFocus();
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.createActivity);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = inflater.inflate(R.layout.create_activity_dialog, null);
        builder.setView(v);

        _nameText = v.findViewById(R.id.input_addedActivityName);
        _btnAdd = v.findViewById(R.id.btn_add);
        _btnCancel = v.findViewById(R.id.btn_cancel);
        _inputLayout_addedActivityName = v.findViewById(R.id.inputLayout_addedActivityName);

        serverService = DataService.getInstance().service;


        _btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validation du nom de l'activité
                final String name = _nameText.getText().toString().trim();
                if (name.isEmpty() || name.equals("") || name.length() < 1 || name.length() > 35){
                    _inputLayout_addedActivityName.setError(getString(R.string.ErrorName));
                    return;
                }

                _btnAdd.setEnabled(false);
                progressD = ProgressDialog.show(getContext(), getString(R.string.PleaseWait),
                        getString(R.string.ServerAnswer), true);

                //Création de l'activité
                final CreateActivityDTO activityToCreate = new CreateActivityDTO(name, getActivity().getIntent().getIntExtra("TripId", 0));
                serverService.createActivity("Bearer " + Token.token.getToken(), activityToCreate).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(getContext(), R.string.ActivityAdded, Toast.LENGTH_SHORT).show();
                            NewBus.bus.post(new EventNewActivity(name, activityToCreate.tripID));
                        }
                        else{
                            Log.i("Retrofit", "code " + response.code());
                            Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
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

        _btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

}
