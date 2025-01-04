package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthOptions;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {

    private EditText emailPhoneInput, passwordInput;
    private Button loginButton;
    private ImageView facebookIcon, googleIcon, linkedinIcon;
    private TextView t;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailPhoneInput = findViewById(R.id.email_phone_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        facebookIcon = findViewById(R.id.facebook_icon);
        googleIcon = findViewById(R.id.google_icon);
        linkedinIcon = findViewById(R.id.linkedin_icon);
        t = findViewById(R.id.signup);

        fAuth= FirebaseAuth.getInstance();

        // Set social media icons click behavior
        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(login.this, "Login with Facebook", Toast.LENGTH_SHORT).show();
            }
        });

        googleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(login.this, "Login with Google", Toast.LENGTH_SHORT).show();
            }
        });

        linkedinIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(login.this, "Login with LinkedIn", Toast.LENGTH_SHORT).show();
            }
        });

        // Sign-up redirection
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });

        // Set login button behavior
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get text from EditText fields
                String email= emailPhoneInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if ( email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if(!(password.length() == 6))
                {
                    Toast.makeText(login.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    login(email,password);
                }
            }
        });
    }
    private void login(String input, String password) {
        if (input.contains("@")) {
            // Email-based login
            fAuth.signInWithEmailAndPassword(input, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Login successful with email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, home.class); // Replace with your actual home activity
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Phone-based login
            verifyPhoneNumber(emailPhoneInput.getText().toString().trim());
        }
    }

    private void verifyPhoneNumber(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth)
                .setPhoneNumber(phoneNumber) // Phone number to authenticate
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
                .setActivity(this) // Current activity
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Auto-verification completed, sign in directly
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(login.this, "Phone verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // In this case, since we're not using OTP, this part can remain unused.
                        // You could log the verification ID if needed.
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

                // Method to sign in with PhoneAuthCredential
                private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
                    fAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login successful with phone", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, home.class); // Replace with your actual home activity
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
}
