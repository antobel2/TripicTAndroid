package com.club.coolkids.clientandroid.see_users_in_trip;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.models.dtos.SignedInUserDTO;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsInTripDialogFragment extends DialogFragment {

    IDataService serverService;
    private ProgressDialog progressD;
    ListView _lvFriendsInTrip;
    UserAdapter _adapter;
    List<SignedInUserDTO> users = new ArrayList<>();

    public FriendsInTripDialogFragment(){}

    public static FriendsInTripDialogFragment newInstance(String title, int tripId){
        FriendsInTripDialogFragment fragment = new FriendsInTripDialogFragment();
        Bundle args = new Bundle();
        args.putString(String.valueOf(R.string.friendsInTrip), title);
        args.putInt("TripId", tripId);
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

        builder.setTitle(R.string.friendsInTrip);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = inflater.inflate(R.layout.see_friends_dialog, null);
        builder.setView(v);

        _lvFriendsInTrip = v.findViewById(R.id.lvFriendsInTrip);

        _adapter = new UserAdapter(getContext());
        _adapter.addAll(users);
        _lvFriendsInTrip.setAdapter(_adapter);

        serverService = DataService.getInstance().service;
        //Rempli la liste d'utilisateurs faisant partie du voyage
        serverService.getUsersForTrip("Bearer" + Token.token.getToken(), getActivity().getIntent().getIntExtra("TripId", 0)).enqueue(new Callback<List<SignedInUserDTO>>() {
            @Override
            public void onResponse(Call<List<SignedInUserDTO>> call, Response<List<SignedInUserDTO>> response) {
                if (response.isSuccessful()) {
                    List<SignedInUserDTO> lstUsers = response.body();
                    for (SignedInUserDTO u:lstUsers) {
                        users.add(u);
                    }

                    _adapter.addAll(users);
                    _lvFriendsInTrip.setAdapter(_adapter);
                }
                else {
                    Log.i("Retrofit", "code " + response.code());
                    Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                    progressD.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<SignedInUserDTO>> call, Throwable t) {
                Log.i("Retrofit", "code " + t.getMessage());
                Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                progressD.dismiss();
            }
        });


        return builder.create();
    }

}
