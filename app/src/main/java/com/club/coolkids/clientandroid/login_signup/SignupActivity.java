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
import com.club.coolkids.clientandroid.models.Token;
import com.club.coolkids.clientandroid.services.DataService;
import com.club.coolkids.clientandroid.services.IDataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText firstNameText;
    EditText nameText;
    EditText usernameText;
    EditText passwordText;
    EditText confirmPasswordText;
    Button signupButton;
    TextView loginLink;
    TextInputLayout passwordInputLayout;
    TextInputLayout confirmPasswordInputLayout;

    IDataService serverService;

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
                finish();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    //Empêche l'utilisateur de mettre un espace dans son mot de passe
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (passwordText.hasFocus()) {
            if (keyCode == KeyEvent.KEYCODE_SPACE) {
                return false;
            }
        }
        else if (confirmPasswordText.hasFocus()) {
            if (keyCode == KeyEvent.KEYCODE_SPACE) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
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
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.CreatingAccount));
        progressDialog.show();

        String firstName = firstNameText.getText().toString();
        String name = nameText.getText().toString();
        final String username = usernameText.getText().toString();
        final String password = passwordText.getText().toString();
        LogInfo logInfo = new LogInfo(username, password, firstName, name);

        serverService = DataService.getInstance().service;
        serverService.register(logInfo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){ //Si successful, tente de logger l'utilisateur sur-le-champ
                    serverService.signin("application/x-www-form-urlencoded", username, password, "password").enqueue(new Callback<Token>() {
                        @Override
                        public void onResponse(Call<Token> call, Response<Token> response) {
                            if(response.isSuccessful()){
                                Token.token = response.body();
                                Intent i = new Intent(getApplicationContext(),DisplayTrips.class);
                                i.putExtra("Username", username);
                                startActivity(i);
                                progressDialog.dismiss();}
                            else{
                                Log.i("Retrofit", "code " + response.code());
                                Toast.makeText(getApplicationContext(), R.string.serverError + " " + response.code(), Toast.LENGTH_SHORT).show();}}
                        @Override
                        public void onFailure(Call<Token> call, Throwable t) {
                            Log.i("Retrofit", "code " + t.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.serverError + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }});}
                else {//Si unsuccessful, le seul champ que l'api peut refuser est le username, donc erreur sur le username
                    Toast.makeText(getApplicationContext(), R.string.usernameExists, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();}
        });
    }


    public void onSignupSuccess() {
        enableButton(signupButton);
        setResult(RESULT_OK, null);
        finish();
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
            firstNameText.setError(getText(R.string.firstNameError));
            valid = false;
        } else {
            firstNameText.setError(null);
        }

        if (name.isEmpty() || name.length() < 1 || name.length() > 50) {
            nameText.setError(getText(R.string.nameError));
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (username.isEmpty() || username.length() < 5 || username.length() > 35) {
            usernameText.setError(getText(R.string.usernameSignupError));
            valid = false;
        } else {
            usernameText.setError(null);
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
}
