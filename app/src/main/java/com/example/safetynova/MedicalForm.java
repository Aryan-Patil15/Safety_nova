package com.example.safetynova;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MedicalForm extends AppCompatActivity {

    private EditText fullNameInput, ageInput, medicalConditionInput, bloodGroupInput, emergencyContactInput;
    private Spinner genderSpinner;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_form);

        // Initialize views
        fullNameInput = findViewById(R.id.full_name_input);
        ageInput = findViewById(R.id.age_input);
        medicalConditionInput = findViewById(R.id.medical_condition_input);
        bloodGroupInput = findViewById(R.id.blood_group_input);
        emergencyContactInput = findViewById(R.id.emergency_contact_input);
        genderSpinner = findViewById(R.id.gender_spinner);
        submitButton = findViewById(R.id.submit_button);

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
        String bloodGroup = bloodGroupInput.getText().toString().trim();
        String emergencyContact = emergencyContactInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(fullName)) {
            showToast("Please enter your full name");
            return;
        }
        if (TextUtils.isEmpty(age) || !isNumeric(age)) {
            showToast("Please enter a valid age");
            return;
        }
        if (TextUtils.isEmpty(bloodGroup)) {
            showToast("Please enter your blood group");
            return;
        }
        if (TextUtils.isEmpty(emergencyContact) || !isPhoneNumberValid(emergencyContact)) {
            showToast("Please enter a valid emergency contact number");
            return;
        }

        // If validation passes
        showToast("Form Submitted Successfully!\n" +
                "Name: " + fullName + "\n" +
                "Age: " + age + "\n" +
                "Gender: " + gender + "\n" +
                "Medical Condition: " + medicalCondition + "\n" +
                "Blood Group: " + bloodGroup + "\n" +
                "Emergency Contact: " + emergencyContact);
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
        return phone.length() >= 10 && TextUtils.isDigitsOnly(phone);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
