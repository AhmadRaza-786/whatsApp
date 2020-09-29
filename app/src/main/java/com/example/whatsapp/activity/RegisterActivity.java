package com.example.whatsapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapp.R;
import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.UserFirebase;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText nameField, emailField, passwordField;
    private FirebaseAuth authentication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameField = findViewById(R.id.editRegisterName);
        emailField = findViewById(R.id.editRegisterEmail);
        passwordField = findViewById(R.id.editRegisterPassword);


    }

    public void registerUser(final User user) {
        authentication = FirebaseConfig.getFirebaseAuthentication();
        authentication.createUserWithEmailAndPassword(
                user.getEmail(), user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(RegisterActivity.this,
                            "User register success",
                            Toast.LENGTH_SHORT).show();
                    UserFirebase.updateNameUser(user.getName());
                    finish();


                    try {
                        String identifyUser = Base64Custom.encodeBase64(user.getEmail());
                        user.setId(identifyUser);
                        user.save();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    String exception = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthWeakPasswordException e){
                        exception = "Enter a stronger password!";
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        exception= "Please, type a valid email";
                    }catch ( FirebaseAuthUserCollisionException e){
                        exception = "This account has already been registered";
                    }catch (Exception e){
                        exception = "Error registering user: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(RegisterActivity.this,
                            exception,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validateRegisterUser(View view) {
        String textName = nameField.getText().toString();
        String textEmail = emailField.getText().toString();
        String textPassword = passwordField.getText().toString();

        if (!textName.isEmpty()) {
            if (!textEmail.isEmpty()) {
                if (!textPassword.isEmpty()) {

                    User user = new User();
                    user.setEmail(textEmail);
                    user.setName(textName);
                    user.setPassword(textPassword);

                    registerUser(user);

                } else {
                    Toast.makeText(RegisterActivity.this, "Password is required!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(RegisterActivity.this, "Email is required!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(RegisterActivity.this, "Name is required!", Toast.LENGTH_SHORT).show();
        }
    }
}