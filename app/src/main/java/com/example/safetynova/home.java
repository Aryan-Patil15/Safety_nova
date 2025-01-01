package com.example.safetynova;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Load the HomeFragment by default
        loadFragment(new HomeFragment());

        // Set up navigation buttons
        ImageButton navHome = findViewById(R.id.nav_home);
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment());
            }
        });

        // Add listeners for other buttons as needed
        ImageButton navMaps = findViewById(R.id.nav_maps);
        navMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load the Maps fragment or activity here
            }
        });

        ImageButton navMessages = findViewById(R.id.nav_messages);
        navMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load the Messages fragment or activity here
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
