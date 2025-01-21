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
import com.google.android.material.navigation.NavigationView;

public class nav_menu extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;

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
        NavigationView navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();  // Assuming `navigationView` is your NavigationView instance
        MenuItem menuItem = menu.findItem(R.id.lg);

        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
                else {

                }
                return true;
            }
        });
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Toast.makeText(nav_menu.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(nav_menu.this, login.class));
                    finish();
                });
    }
}
