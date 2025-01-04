package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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
                    // Show message for invalid credentials
                    Toast.makeText(login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
