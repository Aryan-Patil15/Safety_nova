package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText otpInput;
    private Button verifyOtpButton;
    private String verificationId;
    private FirebaseUser firebaseUser;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        // Initialize FirebaseAuth instance
        fAuth = FirebaseAuth.getInstance();

        // Get data from the intent
        verificationId = getIntent().getStringExtra("verificationId");
        firebaseUser = (FirebaseUser) getIntent().getSerializableExtra("firebaseUser");

        // Initialize UI components
        otpInput = findViewById(R.id.otp_input);
        verifyOtpButton = findViewById(R.id.verify_otp_button);

        // Set verify button click listener
        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpInput.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            } else {
                verifyOtp(otp);
            }
        });
    }

    private void verifyOtp(String otp) {
        if (verificationId != null) {
            // Create PhoneAuthCredential with the verification ID and OTP
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

            // Link phone credential to the Firebase user
            linkPhoneCredentialToUser(credential);
        } else {
            Toast.makeText(this, "Verification ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void linkPhoneCredentialToUser(PhoneAuthCredential credential) {
        if (firebaseUser != null) {
            firebaseUser.linkWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Phone number verified and linked successfully", Toast.LENGTH_SHORT).show();
                    // Navigate to the home screen or next activity
                    navigateToHome();
                } else {
                    Toast.makeText(this, "Error linking phone: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Firebase user is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, login.class); // Replace with your actual home activity
        startActivity(intent);
        finish();
    }
}
