package com.example.whatsapp.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebase {

    public static String getIdentifyUser() {
        FirebaseAuth user = FirebaseConfig.getFirebaseAuthentication();
        String email = user.getCurrentUser().getEmail();
        String identifyUser = Base64Custom.encodeBase64(email);

        return identifyUser;
    }

    public static FirebaseUser getCurrentUser() {
        FirebaseAuth user = FirebaseConfig.getFirebaseAuthentication();
       return user.getCurrentUser();
    }

    public static boolean updateNameUser(String name) {

        try {
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("profile", "Error update profile name ");
                    }
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean updatePhotoUser(Uri uri) {

      try {
          FirebaseUser user = getCurrentUser();
          UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                  .setPhotoUri(uri)
                  .build();

          user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  if (!task.isSuccessful()) {
                      Log.d("profile", "Error update profile photo ");
                  }
              }
          });
          return true;
      } catch (Exception e) {
          e.printStackTrace();
          return false;
      }

    }

    public static User getUserLoggedDatabase() {
        FirebaseUser firebaseUser = getCurrentUser();

        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());

        if (firebaseUser.getPhotoUrl() == null) {
            user.setPhoto("");
        } else {
            user.setPhoto(firebaseUser.getPhotoUrl().toString());
        }

        return user;
    }
}
