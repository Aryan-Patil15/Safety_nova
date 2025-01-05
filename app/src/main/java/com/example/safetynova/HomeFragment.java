package com.example.safetynova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize button safely
        button = view.findViewById(R.id.btn_emergency_services_fragment);

        // Handle button click for sharing location
        View locationButton = view.findViewById(R.id.btn_emergency_location_fragment);
        if (locationButton != null) {
            locationButton.setOnClickListener(v -> {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Live Location Shared", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Handle button click for emergency services
        if (button != null) {
            button.setOnClickListener(v -> loadFragment(new Emergencyservices()));
        }

        // Handle SOS button click
        View sosButton = view.findViewById(R.id.SOS);
        if (sosButton != null) {
            sosButton.setOnClickListener(v -> {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "SOS clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment == null || getActivity() == null) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Unable to load fragment.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: Adds to back stack for navigation
        fragmentTransaction.commit();
    }
}
