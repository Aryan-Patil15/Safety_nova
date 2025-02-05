package com.example.safetynova;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class Profile extends Fragment {

    private TextView profileNameTextView, birthdayTextView, phoneTextView, trustedContactsTextView,
            emailTextView, medicalInfoTextView, ageTextView;
    private Button editprofilebtn;
    private FirebaseFirestore firestore;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        profileNameTextView = view.findViewById(R.id.name);
        birthdayTextView = view.findViewById(R.id.birthday);
        phoneTextView = view.findViewById(R.id.phone);
        trustedContactsTextView = view.findViewById(R.id.trusted_contacts);
        emailTextView = view.findViewById(R.id.email);
        medicalInfoTextView = view.findViewById(R.id.medical_info);
        editprofilebtn=view.findViewById(R.id.edit_profile_button);

        medicalInfoTextView.setOnClickListener(v -> {
            Fragment fragment = new Medical();
            if (fragment != null && getActivity() != null) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Unable to load fragment.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        trustedContactsTextView.setOnClickListener(v -> {
            if (user != null) {
                String userId = user.getUid();

                firestore.collection("User").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                List<String> trustedContacts = (List<String>) documentSnapshot.get("TrustedContacts");
                                List<String> trustedNames = (List<String>) documentSnapshot.get("TrustedNames");

                                if (trustedContacts != null && !trustedContacts.isEmpty()) {
                                    StringBuilder contactsBuilder = new StringBuilder();
                                    for (int i=0;i<trustedContacts.size();i++) {
                                        contactsBuilder.append(trustedNames.get(i)).append(":").append(trustedContacts.get(i)).append("\n");
                                    }
                                    showSelectedContactsAlert(contactsBuilder.toString());
                                } else {
                                    showAlertDialog("No contacts selected.");
                                }
                            } else {
                                showAlertDialog("User profile not found.");
                            }
                        })
                        .addOnFailureListener(e -> {
                            showAlertDialog("Error fetching profile: " + e.getMessage());
                        });
            } else {
                showAlertDialog("User not authenticated.");
            }
        });

        editprofilebtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditProfile.class));
        });


        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        if (user == null) {
            Toast.makeText(requireContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        firestore.collection("User").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userD = (Map<String, Object>) documentSnapshot.get("User_data");
                        Map<String, Object> medicalForm = (Map<String, Object>) documentSnapshot.get("Medical_Form");

                        if (userD != null && medicalForm !=null) {
                            String fullName = (String) userD.get("full_name");
                            String birthday = (String) userD.get("dob");
                            String phone = (String) userD.get("phone");
                            String email = user.getEmail();

                            profileNameTextView.setText(fullName != null ? fullName : "N/A");
                            birthdayTextView.setText(birthday != null ? birthday : "N/A");
                            phoneTextView.setText(phone != null ? phone : "N/A");
                            emailTextView.setText(email != null ? email : "N/A");
                        } else {
                            Toast.makeText(requireContext(), "User data or Medical Form not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error fetching profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Contacts Selection")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showSelectedContactsAlert(String contacts) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Selection")
                .setMessage(contacts)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Edit", (dialog, which) -> dialog.dismiss())
                .show();
    }
}