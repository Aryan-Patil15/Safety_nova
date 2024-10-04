package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DURATION = 1000; // Splash screen duration in milliseconds (3 seconds)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); // Reference your splash screen layout

        // Using Handler to create a delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splash.this, login.class);
            startActivity(intent);
            finish(); // Close this activity so it's not part of the back stack
        }, SPLASH_SCREEN_DURATION);
    }
}
