package com.example.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.whatsapp.R;
import com.example.whatsapp.config.FirebaseConfig;
import com.example.whatsapp.fragment.ContactsFragment;
import com.example.whatsapp.fragment.ConversationsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authentication;

    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        authentication = FirebaseConfig.getFirebaseAuthentication();

        smartTabLayout = findViewById(R.id.viewPagerTab);
        viewPager = findViewById(R.id.viewPager);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Conversations",ConversationsFragment.class)
                .add("Contacts", ContactsFragment.class)
                .create());

        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuSignOut:
                logOutUser();
                finish();
                break;
            case R.id.menuConfiguration:
                openConfiguration();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logOutUser() {

        try {
            authentication.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openConfiguration() {
        Intent intent = new Intent(MainActivity.this, ConfigrationActivity.class);
        startActivity(intent);
    }
}