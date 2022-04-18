package com.example.foodswapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private final int GOOGLE_SIGN_IN = 100;
    private FirebaseAuth auth;
    private Button btnLogin;
    private Button btnRegistro;
    private Button btnGoogle;
    private EditText etEmail;
    private EditText etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        btnGoogle = findViewById(R.id.buttonGoogle);
        btnRegistro = findViewById(R.id.buttonRegistro);
        btnLogin = findViewById(R.id.buttonAcceder);
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etPass = findViewById(R.id.editTextTextPassword);

        sesionIniciada();

        //Setup
        setup();

    }

    private void sesionIniciada(){

        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email = prefs.getString("email",null);
        String provider = prefs.getString("provider",null);

        if(email!=null && provider!=null){
            showHome(email, HomeActivity.ProviderType.valueOf(provider));
        }

    }

    private void setup(){

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();

                if(!email.isEmpty() && !pass.isEmpty()) {

                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                showHome(task.getResult().getUser().getEmail(), HomeActivity.ProviderType.BASIC);
                                //Envía un correo de verificación al usuario que se ha registrado
                                //auth.getCurrentUser().sendEmailVerification();
                            } else {
                                showAlert(task.getException().getMessage());
                            }
                        }

                    });
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();

                if(!email.isEmpty() && !pass.isEmpty()) {

                    auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                showHome(task.getResult().getUser().getEmail(), HomeActivity.ProviderType.BASIC);
                            } else {
                                showAlert(task.getException().getMessage());
                            }
                        }

                    });
                }
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.web_client)).requestEmail().build();

                GoogleSignInClient signInClient = GoogleSignIn.getClient(getApplicationContext(),signInOptions);
                startActivityForResult(signInClient.getSignInIntent(),GOOGLE_SIGN_IN);

            }
        });

    }

    private void showAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        //builder.setMessage("Se ha producido un error autenticando al usuario");
        builder.setMessage(message);
        builder.setPositiveButton("Aceptar",null);
        Dialog dialog  = builder.create();
        dialog.show();
    }

    private void showHome(String email, HomeActivity.ProviderType provider){

        Intent homeIntent = new Intent(this,HomeActivity.class);
        homeIntent.putExtra("email",email);
        homeIntent.putExtra("provider",provider.name());
        startActivity(homeIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE_SIGN_IN){
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = null;
            try {
                account = (GoogleSignInAccount) task.getResult(ApiException.class);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            if(account!=null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                GoogleSignInAccount finalAccount = account;
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showHome(finalAccount.getEmail(), HomeActivity.ProviderType.GOOGLE);
                        } else {
                            showAlert("Fallo registro google");
                        }
                    }
                });
            }
        }
    }
}