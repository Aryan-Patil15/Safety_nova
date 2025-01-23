package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MedicalForm extends AppCompatActivity {

    private EditText fullNameInput, ageInput, medicalConditionInput, emergencyContactInput;
    private Spinner genderSpinner,bloodGroupInput;
    private Button submitButton;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_form);

        // Initialize Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Initialize views
        fullNameInput = findViewById(R.id.full_name_input);
        ageInput = findViewById(R.id.age_input);
        medicalConditionInput = findViewById(R.id.medical_condition_input);
        bloodGroupInput = findViewById(R.id.blood_group_input);
        emergencyContactInput = findViewById(R.id.emergency_contact_input);
        genderSpinner = findViewById(R.id.gender_spinner);
        submitButton = findViewById(R.id.submit_button);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        // Set submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmitForm();
            }
        });
    }

    private void validateAndSubmitForm() {
        // Get input values
        String fullName = fullNameInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem() != null ? genderSpinner.getSelectedItem().toString() : "";
        String medicalCondition = medicalConditionInput.getText().toString().trim();
        String bloodGroup = bloodGroupInput.getSelectedItem() != null ? bloodGroupInput.getSelectedItem().toString() : "";
        String emergencyContact = emergencyContactInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(fullName)) {
            showToast("Please enter your full name");
            return;
        }
        if (TextUtils.isEmpty(age) || !isNumeric(age) || Integer.parseInt(age) <= 0 || Integer.parseInt(age) > 120) {
            showToast("Please enter a valid age (1-120)");
            return;
        }
        if (TextUtils.isEmpty(gender) || gender.equals("Select Gender")) {
            showToast("Please select a valid gender");
            return;
        }
        if (TextUtils.isEmpty(bloodGroup)) {
            showToast("Please enter your blood group");
            return;
        }
        if (TextUtils.isEmpty(emergencyContact) || !isPhoneNumberValid(emergencyContact)) {
            showToast("Please enter a valid 10-digit emergency contact number");
            return;
        }

        // Submit data to Firebase Firestore
        submitDataToFirestore(fullName, age, gender, medicalCondition, bloodGroup, emergencyContact);
    }

    private void submitDataToFirestore(String fullName, String age, String gender, String medicalCondition, String bloodGroup, String emergencyContact) {
        // Create a map of the data
        Map<String, Object> Medical_Form = new HashMap<>();
        Medical_Form.put("full_name", fullName);
        Medical_Form.put("age", Integer.parseInt(age)); // Store age as an integer
        Medical_Form.put("gender", gender);
        Medical_Form.put("medical_condition", TextUtils.isEmpty(medicalCondition) ? "None" : medicalCondition);
        Medical_Form.put("blood_group", bloodGroup.toUpperCase()); // Store blood group in uppercase
        Medical_Form.put("emergency_contact", emergencyContact);

        // Add data to Firestore
        firebaseFirestore.collection("User")
                .document(user.getUid())
                .set(Medical_Form, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    showToast("Form Submitted Successfully!");
                    navigateToLogin();
                })
                .addOnFailureListener(e -> showToast("Failed to submit form: " + e.getMessage()));
    }
    private void navigateToLogin() {
        startActivity(new Intent(this, TrustedContactsSelect.class));
    }
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPhoneNumberValid(String phone) {
        return phone.length() == 10 && TextUtils.isDigitsOnly(phone);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
