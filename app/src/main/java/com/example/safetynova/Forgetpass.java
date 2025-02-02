package com.example.safetynova;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Forgetpass extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnResetPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);

        // Initialize UI elements
        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnresetpassword);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Handle reset password button click
        btnResetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Forgetpass.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send password reset email
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Forgetpass.this, "Password reset link sent to your email", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Forgetpass.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
