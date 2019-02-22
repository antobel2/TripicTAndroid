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
import com.club.coolkids.clientandroid.models.LogInfo;
import com.club.coolkids.clientandroid.models.NewBus;
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText firstNameText;
    private EditText nameText;
    private EditText usernameText;
    private EditText passwordText;
    private EditText confirmPasswordText;
    private Button signupButton;
    private TextView loginLink;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;
    private TextInputLayout firstnameInputLayout;
    private TextInputLayout nameInputLayout;
    private TextInputLayout usernameInputLayout;

    private IDataService serverService;
    private ProgressDialog progressDialog;

    private String username;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        setTitle(R.string.btnSignup);

        usernameText = findViewById(R.id.input_username);
        passwordText = findViewById(R.id.input_passwordSignup);
        firstNameText = findViewById(R.id.input_firstName);
        nameText = findViewById(R.id.input_name);
        signupButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);
        confirmPasswordText = findViewById(R.id.input_confirmPassword);
        passwordInputLayout = findViewById(R.id.inputLayout_passwordSignup);
        confirmPasswordInputLayout = findViewById(R.id.inputLayout_confirmPassword);
        firstnameInputLayout = findViewById(R.id.inputLayout_firstname);
        nameInputLayout = findViewById(R.id.inputLayout_name);
        usernameInputLayout = findViewById(R.id.inputLayout_usernameSignup);

        //Bouton pour s'enregistrer
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        //Lien pour se connecter
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    //Méthode pour s'enregistrer
    public void signup() {
        Log.d(TAG, "Signup");
        if (!validate()) {
            onSignupFailed();
            return;
        }
        disableButton(signupButton);
        //Progress dialog
        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.CreatingAccount));
        progressDialog.show();

        String firstName = firstNameText.getText().toString();
        String name = nameText.getText().toString();
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        LogInfo logInfo = new LogInfo(username, password, firstName, name);

        serverService = DataService.getInstance().service;
        serverService.register(logInfo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){ //Si successful, tente de logger l'utilisateur sur-le-champ
                    login();
                }
                else {//Si unsuccessful, le seul champ que l'api peut refuser est le username, donc erreur sur le username
                    usernameInputLayout.setError(getText(R.string.usernameExists));
                    progressDialog.dismiss();
                    enableButton(signupButton);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                enableButton(signupButton);
            }
        });
    }


    public void login() {
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        serverService.signin("application/x-www-form-urlencoded", username, password, "password").enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()){
                    disableButton(signupButton);
                    Token.token = response.body();
                    Intent i = new Intent(getApplicationContext(),DisplayTrips.class);
                    i.putExtra("Username", username);
                    progressDialog.dismiss();
                    startActivity(i);}
                else{
                    Log.i("Retrofit", "code " + response.code());
                    Toast.makeText(getApplicationContext(), R.string.serverError + " " + getString(response.code()), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();}}
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.i("Retrofit", "code " + t.getMessage());
                Toast.makeText(getApplicationContext(), R.string.serverError + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                enableButton(signupButton);
            }});
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(),  R.string.signupFailed, Toast.LENGTH_LONG).show();
        enableButton(signupButton);
    }

    //Validation des champs du formulaire
    public boolean validate() {
        boolean valid = true;

        String firstName = firstNameText.getText().toString();
        String name = nameText.getText().toString();
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();

        if (firstName.isEmpty() || firstName.length() < 1 || firstName.length() > 50) {
            firstnameInputLayout.setError(getText(R.string.firstNameError));
            valid = false;
        } else {
            firstnameInputLayout.setError(null);
        }

        if (name.isEmpty() || name.length() < 1 || name.length() > 50) {
            nameInputLayout.setError(getText(R.string.nameError));
            valid = false;
        } else {
            nameInputLayout.setError(null);
        }

        if (username.isEmpty() || username.length() < 5 || username.length() > 35) {
            usernameInputLayout.setError(getText(R.string.usernameSignupError));
            valid = false;
        } else {
            usernameInputLayout.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 35) {
            passwordInputLayout.setError(getText(R.string.passwordCreatingError));
            valid = false;
        } else {
            passwordInputLayout.setError(null);
        }

        if (confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError(getText(R.string.passwordConfirmingError));
            valid = false;
        } else {
            confirmPasswordInputLayout.setError(null);
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
