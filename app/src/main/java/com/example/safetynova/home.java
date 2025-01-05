package com.example.safetynova;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class home extends AppCompatActivity {

    private ImageButton btnhome, btnmap;
    private ImageView btnuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initial fragment load
        loadFragment(new HomeFragment());

        // Find views
        btnmap = findViewById(R.id.nav_maps);
        btnhome = findViewById(R.id.nav_home);
        btnuser = findViewById(R.id.user_icon);

        // Set click listeners
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragmentWithDelay(new HomeFragment(), 1000);
            }
        });

        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragmentWithDelay(new MapFragment(), 1000);
            }
        });

        btnuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Profile());
            }
        });
    }

    // Method to load a fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        // Avoid reloading the same fragment
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // Method to load a fragment with delay
    private void loadFragmentWithDelay(Fragment fragment, int delayMillis) {
        new android.os.Handler().postDelayed(() -> {
            if (!isDestroyed()) { // Ensure activity is still running
                loadFragment(fragment);
            }
        }, delayMillis);
    }
}
