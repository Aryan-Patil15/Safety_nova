package com.example.safetynova;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Forgetpass extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnResetPassword;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnresetpassword);

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        // Set button click listener for password reset
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        // Check if the email field is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send the password reset email
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Password reset link sent to your email", Toast.LENGTH_LONG).show();
                finish();
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }

        });
    }
}
