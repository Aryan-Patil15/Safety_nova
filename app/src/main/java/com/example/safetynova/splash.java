package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splash extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DURATION = 1000; // Splash screen duration in milliseconds (3 seconds)
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); // Reference your splash screen layout

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        // Using Handler to create a delay
        new Handler().postDelayed(() -> {
            if(user==null || fAuth==null) {
                Intent intent = new Intent(splash.this, login.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent = new Intent(splash.this, home.class);
                startActivity(intent);
                finish();
            }
            // Close this activity so it's not part of the back stack
        }, SPLASH_SCREEN_DURATION);
    }
}
