package com.example.safetynova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Profile extends Fragment {

    // Declare TextView components
    private TextView profileNameTextView, birthdayTextView, phoneTextView, trustedContactsTextView,
            emailTextView, medicalInfoTextView, ageTextView, genderTextView, bloodGroupTextView, emergencyContactTextView;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase components
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Link UI components to their XML counterparts
        profileNameTextView = view.findViewById(R.id.name);
        birthdayTextView = view.findViewById(R.id.birthday);
        phoneTextView = view.findViewById(R.id.phone);
        trustedContactsTextView = view.findViewById(R.id.trusted_contacts);
        emailTextView = view.findViewById(R.id.email);
        medicalInfoTextView = view.findViewById(R.id.medical_info);
        ageTextView = view.findViewById(R.id.age);
        genderTextView = view.findViewById(R.id.gender);
        bloodGroupTextView = view.findViewById(R.id.blood_group);


        // Load user profile data
        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        // Check if user is authenticated
        if (auth.getCurrentUser() == null) {
            Toast.makeText(requireContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        // Fetch data from Firestore
        firestore.collection("User").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve and set fields
                        String fullname = documentSnapshot.getString("full_name");
                        String birthday = documentSnapshot.getString("Birthday");
                        String phone = documentSnapshot.getString("Phone");
                        String email = documentSnapshot.getString("Email");
                        String medicalInfo = documentSnapshot.getString("medical_condition");
                        Long age = documentSnapshot.getLong("age");
                        String gender = documentSnapshot.getString("gender");
                        String bloodGroup = documentSnapshot.getString("blood_group");
                        String emergencyContact = documentSnapshot.getString("emergency_contact");
                        List<String> trustedContacts = (List<String>) documentSnapshot.get("TrustedContacts");

                        // Update UI with the retrieved data
                        profileNameTextView.setText(fullname != null ? fullname : "N/A");
                        birthdayTextView.setText(birthday != null ? birthday : "N/A");
                        phoneTextView.setText(phone != null ? phone : "N/A");
                        emailTextView.setText(email != null ? email : "N/A");
                        medicalInfoTextView.setText(medicalInfo != null ? medicalInfo : "N/A");
                        ageTextView.setText(age != null ? String.valueOf(age) : "N/A");
                        genderTextView.setText(gender != null ? gender : "N/A");
                        bloodGroupTextView.setText(bloodGroup != null ? bloodGroup : "N/A");


                        // Format and display trusted contacts
                        if (trustedContacts != null && !trustedContacts.isEmpty()) {
                            StringBuilder contactsBuilder = new StringBuilder();
                            for (String contact : trustedContacts) {
                                contactsBuilder.append(contact).append("\n");
                            }
                            trustedContactsTextView.setText(contactsBuilder.toString().trim());
                        } else {
                            trustedContactsTextView.setText("No trusted contacts available.");
                        }
                    } else {
                        // No document found for this user
                        Toast.makeText(requireContext(), "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(requireContext(), "Error fetching profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
