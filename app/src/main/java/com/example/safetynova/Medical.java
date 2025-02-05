package com.example.safetynova;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Medical extends Fragment {

    private TextView fullNameText, bloodGroupText, allergiesText, medicalConditionsText, ageText;
    private Button editbtn;
    FirebaseFirestore firestore;
    FirebaseAuth fAuth;
    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medical, container, false);

        // Initialize the TextViews
        fullNameText = view.findViewById(R.id.full_name);
        bloodGroupText = view.findViewById(R.id.blood_group);
        allergiesText = view.findViewById(R.id.allergies);
        medicalConditionsText = view.findViewById(R.id.medical_conditions);
        ageText = view.findViewById(R.id.age);
        editbtn=view.findViewById(R.id.edit_button);

        firestore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();

        editbtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), edit_medical_info.class));

        });

        medical_info();

        return view;
    }
    private void medical_info()
    {
        // Check if user is authenticated
        if (fAuth.getCurrentUser() == null) {
            Toast.makeText(requireContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }
        userId=fAuth.getCurrentUser().getUid();
        // Fetch data from Firestore
        firestore.collection("User").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the Medical_Form object
                        Map<String, Object> medicalForm = (Map<String, Object>) documentSnapshot.get("Medical_Form");
                        Map<String, Object> userD = (Map<String, Object>) documentSnapshot.get("User_data");
                        if (medicalForm != null) {
                            // Extract and set fields from the nested Medical_Form
                            String fullName = (String) userD.get("full_name");
                            String bloodgrp = (String) medicalForm.get("blood_group");
                            String allergies = (String) medicalForm.get("allergies");
                            String medicalcondn = (String) medicalForm.get("medical_condition");
                            Long age = (Long) medicalForm.get("age");

                            fullNameText.setText(fullName!= null ?fullName: "N/A");
                            bloodGroupText.setText(bloodgrp!= null ?bloodgrp: "N/A");
                            allergiesText.setText(allergies!= null ?allergies: "N/A");
                            medicalConditionsText.setText(medicalcondn!= null ?medicalcondn: "N/A");
                            ageText.setText(age!= null ?age.toString(): "N/A");
                        } else {
                            Toast.makeText(requireContext(), "Medical Form not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (!isInternetAvailable(requireContext())) {
                        Toast.makeText(requireContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error fetching profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // For Android Marshmallow (API 23) and above
                android.net.Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                    return capabilities != null &&
                            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
                }
            } else {
                // For older Android versions
                android.net.NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnected();
            }
        }
        return false;
    }
}
