package com.example.whatsapp.model;

import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.helper.UserFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String password;
    private String photo;

    public User() {
    }

    public void save() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference user = firebaseRef.child("users").child(getId());

        user.setValue(this);
    }

    public void update() {
        String identifyUser = UserFirebase.getIdentifyUser();
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();

        DatabaseReference userRef = database.child("users")
                .child(identifyUser);

        Map<String, Object> userValues = convertToMap();

        userRef.updateChildren(userValues);

    }

    @Exclude
    public Map<String, Object> convertToMap() {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail());
        userMap.put("name", getName());
        userMap.put("photo", getPhoto());

        return userMap;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
