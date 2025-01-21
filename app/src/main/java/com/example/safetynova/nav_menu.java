package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class nav_menu extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "onCreate called", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_home); // Ensure this matches your activity's layout

        // Initialize Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("193867853435-l3fh0m7racs996uekvj32ulbmum5chpn.apps.googleusercontent.com") // Replace with your Web Client ID
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Find the NavigationView
        navigationView = findViewById(R.id.nav_view);

        // Set up the navigation menu item click listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.ho) {
                    Toast.makeText(nav_menu.this, "Home Selected", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.in) {
                    Toast.makeText(nav_menu.this, "Info Selected", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.we) {
                    Toast.makeText(nav_menu.this, "Weather Updates Selected", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.lg) {
                    signOut();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true; // Return true to show the menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.lg) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // User has signed out
                        Toast.makeText(nav_menu.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(nav_menu.this, login.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}