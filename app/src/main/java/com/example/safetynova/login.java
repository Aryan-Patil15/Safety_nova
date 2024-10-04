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

public class login extends AppCompatActivity {

    private EditText emailPhoneInput, passwordInput;
    private Button loginButton;
    private ImageView facebookIcon, instagramIcon, linkedinIcon;
    private TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailPhoneInput = findViewById(R.id.email_phone_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        facebookIcon = findViewById(R.id.facebook_icon);
        instagramIcon = findViewById(R.id.instagram_icon);
        linkedinIcon = findViewById(R.id.linkedin_icon);
        t = findViewById(R.id.signup);

        // Set social media icons click behavior
        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(login.this, "Login with Facebook", Toast.LENGTH_SHORT).show();
            }
        });

        instagramIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(login.this, "Login with Instagram", Toast.LENGTH_SHORT).show();
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
                String emailPhone = emailPhoneInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Check if credentials are correct
                if (emailPhone.equals("user") && password.equals("1234")) {
                    // Intent to navigate to home activity
                    Intent intent = new Intent(login.this, home.class);
                    startActivity(intent);
                } else {
                    // Show message for invalid credentials
                    Toast.makeText(login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
