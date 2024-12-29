// HomeFragment.java
package com.example.safetynova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements if needed
        View btnEmergencyLocation = view.findViewById(R.id.btn_emergency_location);
        View btnEmergencyServices = view.findViewById(R.id.btn_emergency_services);
        View btnLocation = view.findViewById(R.id.btn_location);

        // Set click listeners or other interactions if required
        btnEmergencyLocation.setOnClickListener(v -> {
            // Handle Crises Alert button click
        });

        btnEmergencyServices.setOnClickListener(v -> {
            // Handle Emergency Services button click
        });

        btnLocation.setOnClickListener(v -> {
            // Handle SOS button click
        });
    }
}
