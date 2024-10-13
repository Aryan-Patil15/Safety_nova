package com.example.safetynova;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Medical extends AppCompatActivity {

    private TextView dob, allergies, bloodType, height, weight, pregnancyStatus, medications, address, medicalNotes, organDonor;
    private LinearLayout extendedSection;
    private Button moreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);

        // Initialize fields
        dob = findViewById(R.id.dob);
        allergies = findViewById(R.id.allergies);
        bloodType = findViewById(R.id.blood_type);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        pregnancyStatus = findViewById(R.id.pregnancy_status);
        medications = findViewById(R.id.medications);
        address = findViewById(R.id.address);
        medicalNotes = findViewById(R.id.medical_notes);
        organDonor = findViewById(R.id.organ_donor);
        extendedSection = findViewById(R.id.extendedSection);
        moreButton = findViewById(R.id.moreButton);

        // Populate with initial values
        populateMedicalInfo();

        // Set up click listeners for each field to allow editing
        setupClickListeners();

        // Set up More button to show/hide extended fields
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extendedSection.getVisibility() == View.GONE) {
                    extendedSection.setVisibility(View.VISIBLE);
                    moreButton.setText("Less");
                } else {
                    extendedSection.setVisibility(View.GONE);
                    moreButton.setText("More");
                }
            }
        });
    }

    // Populate fields with default values
    private void populateMedicalInfo() {
        dob.setText("Unknown");
        allergies.setText("Unknown");
        bloodType.setText("Unknown");
        height.setText("Unknown");
        weight.setText("Unknown");
        pregnancyStatus.setText("Unknown");
        medications.setText("Unknown");
        address.setText("Unknown");
        medicalNotes.setText("Unknown");
        organDonor.setText("Unknown");
    }

    // Set up click listeners for editing fields
    private void setupClickListeners() {
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Date of Birth", dob);
            }
        });
        allergies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Allergies", allergies);
            }
        });
        bloodType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Blood Type", bloodType);
            }
        });
        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Height", height);
            }
        });
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Weight", weight);
            }
        });
        pregnancyStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Pregnancy Status", pregnancyStatus);
            }
        });
        medications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Medications", medications);
            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Address", address);
            }
        });
        medicalNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Medical Notes", medicalNotes);
            }
        });
        organDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Organ Donor Status", organDonor);
            }
        });
    }

    // Show dialog to edit medical information fields
    private void showEditDialog(String field, final TextView textView) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_field, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.editText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        editText.setHint("Enter " + field);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newValue = editText.getText().toString();
                if (!newValue.isEmpty()) {
                    textView.setText(newValue);
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(Medical.this, "Please enter a valid value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
