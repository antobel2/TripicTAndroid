package com.club.coolkids.clientandroid.login_signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.club.coolkids.clientandroid.R;
import com.club.coolkids.clientandroid.display_trips.DisplayTrips;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText usernameText;
    EditText passwordText;
    Button loginButton;
    TextView signupLink;
    TextInputLayout passwordInputLayout;
    TextInputLayout usernameInputLayout;

    IDataService serverService;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setTitle(R.string.btnLogin);

        usernameText = findViewById(R.id.input_username);
        passwordText = findViewById(R.id.input_passwordLogin);
        loginButton = findViewById(R.id.btn_login);
        signupLink = findViewById(R.id.link_signup);
        passwordInputLayout = findViewById(R.id.inputLayout_passwordLogin);
        usernameInputLayout = findViewById(R.id.inputLayout_username);

        //Bouton pour se logger
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        //Lien pour se créer un compte
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        disableButton(loginButton);

        //Progress dialog
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.Athenticating));
        progressDialog.show();

        serverService = DataService.getInstance().service;

        final String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        serverService.signin("application/x-www-form-urlencoded", username, password, "password").enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()){
                    disableButton(loginButton);
                    Token.token = response.body();
                    Intent i = new Intent(getApplicationContext(),DisplayTrips.class);
                    i.putExtra("Username", username);
                    progressDialog.dismiss();
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else
                {
                    Log.i("Retrofit", "code " + response.code());
                    passwordInputLayout.setError(getString(R.string.connectionError));
                    progressDialog.dismiss();
                    enableButton(loginButton);
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.i("Retrofit", "code " + t.getMessage());
                Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                enableButton(loginButton);
            }
        });
    }

    public void onLoginFailed() {
        enableButton(loginButton);
    }

    public boolean validate() {
        boolean valid = true;

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 5 || username.length() > 35) {
            usernameInputLayout.setError(getString(R.string.usernameError));
            valid = false;
        } else {
            usernameInputLayout.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 35) {
            passwordInputLayout.setError(getString(R.string.passwordError));
            valid = false;
        } else {
            passwordInputLayout.setError(null);
        }

        return valid;
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
