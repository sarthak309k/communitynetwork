package com.example.communitiesnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragment_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragment_container = findViewById(R.id.fragment_container);

        // Select the "Community" tab by default
        bottomNavigationView.setSelectedItemId(R.id.community_id);

        // Replace the FrameLayout with the CommunityList fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CommunityList())
                .commit();
        bottomNavigationView.setSelectedItemId(R.id.community_id);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 if (item.getItemId() == R.id.events_id) {
                   getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EventAlertFragment()).commit();
                } else if (item.getItemId() == R.id.user_profile_id) {
                    // Handle user profile tab
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new UserProfile())
                            .commit();
                } else if (item.getItemId() == R.id.community_id) {
                    // Replace the FrameLayout with the CommunityList fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new CommunityList())
                            .commit();
                }
                return true;
            }
        });
    }
}