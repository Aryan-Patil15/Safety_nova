package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class home extends AppCompatActivity {

    private Button btnEmergencyLocation, btnEmergencyServices;
    private ImageButton btnLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Ensure your layout includes a FrameLayout with ID fragment_container

        // Initialize buttons
        btnEmergencyLocation = findViewById(R.id.btn_emergency_location);
        btnEmergencyServices = findViewById(R.id.btn_emergency_services);
        btnLocation = findViewById(R.id.nav_maps);

        // Set click listeners
        btnEmergencyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home.this, "Emergency Location Sharing clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnEmergencyServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home.this, "Emergency Services clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, Map.class);
                startActivity(intent);
            }
        });

        // Load the HomeFragment into the container
        loadFragment(new HomeFragment());
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
