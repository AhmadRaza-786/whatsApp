package com.example.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapp.R;
import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailField, passwordField;
    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authentication = FirebaseConfig.getFirebaseAuthentication();

        emailField = findViewById(R.id.editEmail);
        passwordField = findViewById(R.id.editPassword);

    }

    public void loginUser(User user) {

        authentication.signInWithEmailAndPassword(
                user.getEmail(), user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    openPrincipalActivity();
                } else {
                    String exception = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        exception = "User is not registered.";
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        exception = "E-mail and password do not correspond to a registered user";
                    }catch (Exception e){
                        exception = "Error registering user: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this,
                            exception,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void validateAuthenticationUser(View view) {
        String textEmail = emailField.getText().toString();
        String textPassword = passwordField.getText().toString();

        if (!textEmail.isEmpty()) {
            if (!textPassword.isEmpty()) {

                User user = new User();
                user.setEmail(textEmail);
                user.setPassword(textPassword);

                loginUser(user);

            } else {
                Toast.makeText(LoginActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = authentication.getCurrentUser();
        if (currentUser != null) {
            openPrincipalActivity();
        }
    }

    public void openRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openPrincipalActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}