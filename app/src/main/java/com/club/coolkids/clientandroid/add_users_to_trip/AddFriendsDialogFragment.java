package com.club.coolkids.clientandroid.add_users_to_trip;

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
import com.club.coolkids.clientandroid.models.dtos.InviteUserToTripDTO;
import com.club.coolkids.clientandroid.models.dtos.SignedInUserDTO;
import com.club.coolkids.clientandroid.models.dtos.UserSearchResultDTO;
import com.club.coolkids.clientandroid.events.EventDeleteFriend;
import com.club.coolkids.clientandroid.events.EventGetUserSearchResult;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendsDialogFragment extends DialogFragment {

    AutoCompleteTextView _nameText;
    Button _btnAdd;
    Button _btnCancel;
    Button _btn_addAll;
    IDataService serverService;
    TextInputLayout _inputLayout_addedFriendName;
    ListView _lvAddFriends;
    UserSearchResultAdapter _adapter;
    List<UserSearchResultDTO> users = new ArrayList<>();
    AddFriendAdapter _adapterLowerList;
    List<SignedInUserDTO> usersLowerList = new ArrayList<>();
    UserSearchResultDTO mUser;

    private ProgressDialog progressD;

    public AddFriendsDialogFragment(){}

    public static AddFriendsDialogFragment newInstance(String title, int tripId){
        AddFriendsDialogFragment fragment = new AddFriendsDialogFragment();
        Bundle args = new Bundle();
        args.putString(String.valueOf(R.string.addFriends), title);
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

        builder.setTitle(R.string.addFriends);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = inflater.inflate(R.layout.add_friends_dialog, null);
        builder.setView(v);

        _nameText = v.findViewById(R.id.actv_findFriends);
        _btnAdd = v.findViewById(R.id.btn_add);
        _btnCancel = v.findViewById(R.id.btn_cancel);
        _btn_addAll = v.findViewById(R.id.btn_addAll);
        _inputLayout_addedFriendName = v.findViewById(R.id.inputLayout_findFriends);
        _lvAddFriends = v.findViewById(R.id.lvAddFriends);

        ArrayList<UserSearchResultDTO> arrayList = new ArrayList<>();
        ArrayList<SignedInUserDTO> arrayList2 = new ArrayList<>();

        //Rempli la liste du bas
        _adapterLowerList = new AddFriendAdapter(getActivity().getApplicationContext());
        //_adapterLowerList.addAll(usersLowerList);
        //_lvAddFriends.setAdapter(_adapterLowerList);
        _lvAddFriends.setAdapter(new ArrayAdapter<SignedInUserDTO>(getContext(), R.layout.add_friends_list_item, arrayList2));

        _adapter = new UserSearchResultAdapter(getActivity().getApplicationContext());
        //_nameText.setAdapter(_adapter);
        _nameText.setThreshold(1);
        _nameText.setDropDownAnchor(R.id.inputLayout_findFriends);
        _nameText.setAdapter(new ArrayAdapter<UserSearchResultDTO>(getContext(), R.layout.see_friends_list_item, arrayList));

        serverService = DataService.getInstance().service;

        _nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                try{
                    Log.e("AutoComplete", "User input: " + s);
                    // update the adapater
                    _adapter.notifyDataSetChanged();

                    if (s.toString() != "" && s.length() != 0 && _nameText.getText().toString().trim() != ""){
                        //Rempli la liste de suggestions
                        serverService.findUsers("Bearer " + Token.token.getToken(), getActivity().getIntent().getIntExtra("TripId", 0), s.toString()).enqueue(new Callback<List<UserSearchResultDTO>>() {
                            @Override
                            public void onResponse(Call<List<UserSearchResultDTO>> call, Response<List<UserSearchResultDTO>> response) {
                                if (response.isSuccessful()) {
                                    users.clear();
                                    List<UserSearchResultDTO> lstUsers = response.body();
                                    if (lstUsers != null && lstUsers.size() != 0) {
                                        for (UserSearchResultDTO u:lstUsers) {
                                            users.add(u);
                                        }
                                        _adapter.addAll(users);
                                        _nameText.setAdapter(_adapter);
                                        _nameText.showDropDown();
                                    }
                                    else {
                                        _nameText.dismissDropDown();
                                    }

                                }
                                else {
                                    Log.i("Retrofit", "code " + response.code());
                                    Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<UserSearchResultDTO>> call, Throwable t) {
                                Log.i("Retrofit", "code " + t.getMessage());
                                Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                                progressD.dismiss();
                            }
                        });
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Ajoute un utilisateur dans la liste du bas
        _btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validation du nom de l'activité
                final String name = _nameText.getText().toString().trim();
                Log.i("EVENT", "Event Add User");
                UserSearchResultDTO u = mUser;
                SignedInUserDTO user = new SignedInUserDTO(u.userId, u.userName, u.firstName, u.lastName);
                usersLowerList.add(user);
                _adapterLowerList.add(user);
                _lvAddFriends.setAdapter(_adapterLowerList);
                mUser = null;
                _nameText.setText("");
            }
        });

        _btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Ajoute les users de la liste du bas à la liste permanente
        _btn_addAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteUserToTripDTO i = new InviteUserToTripDTO(getActivity().getIntent().getIntExtra("TripId", 0));
                List<String> lst = new ArrayList<>();
                for (SignedInUserDTO u:usersLowerList) {
                    lst.add(u.uuid);
                }
                i.userIds = lst;
                serverService.inviteUserToTrip("Bearer " + Token.token.getToken(), i).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), R.string.usersAdded, Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                        else {
                            Log.i("Retrofit", "code " + response.code());
                            Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.i("Retrofit", "code " + t.getMessage());
                        Toast.makeText(getContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setView(v);

        return builder.create();
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
    public void clickOnUserEvent(EventGetUserSearchResult e){
        String txt = e.userDTO.toString();
        int i = 0;
        _nameText.setText(txt);
        _nameText.performCompletion();
        _nameText.dismissDropDown();
        mUser = e.userDTO;
    }

    @Subscribe
    public void deleteFriend(EventDeleteFriend e){
        Log.i("EVENT", "Event Delete User");
        SignedInUserDTO user = e.userDTO;
        usersLowerList.remove(user);
        _adapterLowerList.remove(user);
        _lvAddFriends.setAdapter(_adapterLowerList);
    }

}
