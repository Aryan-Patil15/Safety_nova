package com.example.safetynova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Medical extends Fragment {

    private TextView fullNameText, bloodGroupText, allergiesText, medicalConditionsText, ageText;

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

        // Set example data
        fullNameText.setText("Jane Doe");
        bloodGroupText.setText("A+");
        allergiesText.setText("Nuts, Dust");
        medicalConditionsText.setText("Hypertension");
        ageText.setText("+987 654 3210");

        return view;
    }
}
