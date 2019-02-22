package com.club.coolkids.clientandroid.create_trip;

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
import com.club.coolkids.clientandroid.models.dtos.CreateTripDTO;
import com.club.coolkids.clientandroid.events.EventNewTrip;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripDialogFragment extends DialogFragment {

    EditText _nameText;
    Button _btnAdd;
    Button _btnCancel;
    IDataService serverService;
    TextInputLayout _inputLayout_addedTripItem;
    private ProgressDialog progressD;

    public TripDialogFragment(){}

    public static TripDialogFragment newInstance(String title){
        TripDialogFragment fragment = new TripDialogFragment();
        Bundle args = new Bundle();
        args.putString(String.valueOf(R.string.createTrip), title);
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

        builder.setTitle(R.string.createTrip);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = inflater.inflate(R.layout.create_trip_dialog, null);
        builder.setView(v);

        _nameText = v.findViewById(R.id.input_addedTripName);
        _btnAdd = v.findViewById(R.id.btn_add);
        _btnCancel = v.findViewById(R.id.btn_cancel);
        _inputLayout_addedTripItem = v.findViewById(R.id.inputLayout_addedTripName);

        serverService = DataService.getInstance().service;


        _btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validation du nom de l'activité
                final String name = _nameText.getText().toString();
                if (name.isEmpty() || name.equals("") || name.length() < 1 || name.length() > 35){
                    _inputLayout_addedTripItem.setError(getString(R.string.ErrorName));
                    return;
                }

                _btnAdd.setEnabled(false);
                progressD = ProgressDialog.show(getContext(), getString(R.string.PleaseWait),
                        getString(R.string.ServerAnswer), true);

                //Création du voyage
                CreateTripDTO tripToCreate = new CreateTripDTO(name);
                serverService.createTrip("Bearer " + Token.token.getToken(), tripToCreate).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(getContext(), R.string.TripAdded, Toast.LENGTH_SHORT).show();
                            NewBus.bus.post(new EventNewTrip(name));
                            progressD.dismiss();
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
