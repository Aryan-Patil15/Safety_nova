package com.example.safetynova;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class home extends AppCompatActivity {

    private ImageButton btnhome, btnmap;
    private ImageView side, btnuser;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initial fragment load
        loadFragment(new HomeFragment());

        // Find views
        side = findViewById(R.id.side);
        btnmap = findViewById(R.id.nav_maps);
        btnhome = findViewById(R.id.nav_home);
        btnuser = findViewById(R.id.user_icon);
        drawerLayout = findViewById(R.id.activity_home);
        navigationView = findViewById(R.id.nav_view);

        // Menu icon click listener
        side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) { // Open drawer if not already open
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

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

        // Set up navigation item selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return handleNavigationItemSelected(item);
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

    // Handle navigation drawer item selection
    private boolean handleNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ho) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()) // Ensure HomeFragment() is valid
                    .commit();
        } else if (item.getItemId() == R.id.in) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new About()) // Ensure About() is valid
                    .commit();
        } else if (item.getItemId() == R.id.lg) {
            logoutUser();
            return true;
        }else {
            return false;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;



    }


private void logoutUser() {


    // Redirect to login activity
    Intent intent = new Intent(home.this, login.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);

    // Optionally, show a toast message
    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
}
}