package com.example.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.MessageAdapter;
import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.UserFirebase;
import com.example.whatsapp.model.Message;
import com.example.whatsapp.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewName;
    private CircleImageView circleImageViewPhoto;
    private EditText editMessage;
    private User recipientUser;
    private DatabaseReference database;
    private  DatabaseReference messagesRef;
    private ChildEventListener childEventListenerMessage;

    //identifier users sender and recipient
    private String idUserSender;
    private String idUserRecipient;

    private RecyclerView recyclerMessage;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewName = findViewById(R.id.textChatName);
        circleImageViewPhoto = findViewById(R.id.circleImageChat);
        editMessage = findViewById(R.id.editMessage);
        recyclerMessage = findViewById(R.id.recyclerMessage);

        idUserSender = UserFirebase.getIdentifyUser();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipientUser = (User) bundle.getSerializable("contactChat");
            textViewName.setText(recipientUser.getName());

            String photo = recipientUser.getPhoto();
            if (photo != null) {
                Uri uri = Uri.parse(recipientUser.getPhoto());
                Glide.with(ChatActivity.this)
                        .load(uri)
                        .into(circleImageViewPhoto);
            } else {
                circleImageViewPhoto.setImageResource(R.drawable.padrao);
            }

            //recover data user recipient
            idUserRecipient = Base64Custom.decodeBase64(recipientUser.getEmail());
        }

        adapter = new MessageAdapter(messages, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMessage.setLayoutManager(layoutManager);
        recyclerMessage.setHasFixedSize(true);
        recyclerMessage.setAdapter(adapter);

        database = FirebaseConfig.getFirebaseDatabase();
        messagesRef = database.child("message")
                .child(idUserSender)
                .child(idUserRecipient);
    }

    public void sendMessage(View view) {
        String textMessage = editMessage.getText().toString();

        if (!textMessage.isEmpty()) {
            Message message = new Message();
            message.setIdUser(idUserSender);
            message.setMessage(textMessage);

            //Save message to sender
            saveMessage(idUserSender, idUserRecipient, message);

        } else {
            Toast.makeText(ChatActivity.this, "type a message to send", Toast.LENGTH_LONG).show();
        }
    }

    private void saveMessage(String idSender, String idRecipient, Message msg) {

        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference messageRef = database.child("message");

        messageRef.child(idSender).child(idRecipient)
                .push()
                .setValue(msg);

        editMessage.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverMessage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messagesRef.removeEventListener(childEventListenerMessage);
    }

    private void recoverMessage() {
        childEventListenerMessage = messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messages.add(message);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}