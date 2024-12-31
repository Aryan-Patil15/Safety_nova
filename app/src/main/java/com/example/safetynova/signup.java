package com.example.safetynova;

import android.app.DatePickerDialog;
import android.content.Intent;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import java.util.Calendar;

public class signup extends AppCompatActivity {

    private EditText emailInput, phoneInput, passwordInput, confirmPasswordInput, dobInput, fullNameInput;
    private Button signUpButton;
    private TextView loginText;
    boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        fullNameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        dobInput = findViewById(R.id.dob_input);  // Date of Birth Input
        signUpButton = findViewById(R.id.signup_button);
        loginText = findViewById(R.id.login);
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        isPasswordVisible = false;
        // Set Date of Birth field click listener
        dobInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set sign-up button click behavior
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameInput.getText().toString();
                String email = emailInput.getText().toString();
                String phone = phoneInput.getText().toString();
                String dob = dobInput.getText().toString();  // Get date of birth input

                if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // Add your sign-up logic here
                    Toast.makeText(signup.this, "Signing up...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Navigate to Login activity
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, login.class);
                startActivity(intent);
            }
        });
        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Optional: Implement actions before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Dynamically fetch the password value
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                // Compare passwords and set error
                if (!confirmPassword.equals(password)) {
                    confirmPasswordInput.setError("Passwords do not match");
                } else {
                    confirmPasswordInput.setError(null); // Clear the error
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Optional: Implement actions after text is changed
            }
        });


    }


    // Method to show Date Picker Dialog
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(signup.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set selected date to the dobInput field
                        dobInput.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}
