package com.example.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.helper.Permission;
import com.example.whatsapp.helper.UserFirebase;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigrationActivity extends AppCompatActivity {
    private String[] necessaryPermission = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera, imageButtonGallery;
    private static final int SELECTION_CAMERA = 100;
    private static final int SELECTION_GALLERY = 200;
    private CircleImageView circleImageView;
    private EditText editProfileName;
    private ImageView imageUpdateName;
    private StorageReference storageReference;
    private String identifyUser;
    private User userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configration);

        storageReference = FirebaseConfig.getFirebaseStorage();
        identifyUser = UserFirebase.getIdentifyUser();
        userLogged = UserFirebase.getUserLoggedDatabase();

        Permission.validatePermission(necessaryPermission, this, 1);

        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGallery = findViewById(R.id.imageButtonGallery);
        circleImageView = findViewById(R.id.circleImageViewPhotoProfile);
        editProfileName = findViewById(R.id.editProfileName);
        imageUpdateName = findViewById(R.id.imageUpdateName);


        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configuration");
        setSupportActionBar(toolbar);

        FirebaseUser user = UserFirebase.getCurrentUser();
       Uri url = user.getPhotoUrl();
       if (url != null) {
           Glide.with(ConfigrationActivity.this)
                   .load(url)
                   .into(circleImageView);

       } else {
           circleImageView.setImageResource(R.drawable.padrao);
       }

       editProfileName.setText(user.getDisplayName());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECTION_CAMERA);
                }
            }
        });

        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECTION_GALLERY);
                }
            }
        });

        imageUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editProfileName.getText().toString();
                boolean returns = UserFirebase.updateNameUser(name);

                if (returns) {

                    userLogged.setName(name);
                    userLogged.update();

                    Toast.makeText(ConfigrationActivity.this, "Name changed successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case SELECTION_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;

                    case SELECTION_GALLERY:
                        Uri localImageSelection = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImageSelection);
                        break;
                }

                if (image != null) {
                    circleImageView.setImageBitmap(image);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dataImage = baos.toByteArray();

                    final StorageReference imageRef = storageReference
                            .child("images")
                            .child("profile")
                            .child(identifyUser + ".png");

                    UploadTask uploadTask = imageRef.putBytes(dataImage);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigrationActivity.this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfigrationActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri uri = task.getResult();
                                    uploadUserPhoto(uri);
                                }
                            });
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

  public void uploadUserPhoto(Uri uri) {
       boolean returns =  UserFirebase.updatePhotoUser(uri);
       if (returns) {
           userLogged.setPhoto(uri.toString());
           userLogged.update();

           Toast.makeText(ConfigrationActivity.this, "Image changed successfully", Toast.LENGTH_SHORT).show();
       }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult : grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                alertValidatePermission();
            }
        }
    }

    private void alertValidatePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("To use the app you need to accept the permissions");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}