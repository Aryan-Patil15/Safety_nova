package com.example.safetynova;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class home extends AppCompatActivity {

    private ImageButton btnhome, btnmap,btnmsg;
    private ImageView side, btnuser;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Find views
        side = findViewById(R.id.side);
        btnmap = findViewById(R.id.nav_maps);
        btnmsg=findViewById(R.id.nav_messages);
        btnhome = findViewById(R.id.nav_home);
        btnuser = findViewById(R.id.user_icon);
        drawerLayout = findViewById(R.id.activity_home);
        navigationView = findViewById(R.id.nav_view);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        // Initial fragment load

        loadFragment(new HomeFragment());
        highlightTab(btnhome);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("193867853435-l3fh0m7racs996uekvj32ulbmum5chpn.apps.googleusercontent.com") // Replace with actual Web Client ID
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        // Menu icon click listener
        side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) { // Open drawer if not already open
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Set click listeners
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragmentWithDelay(new HomeFragment(), 1000);
                highlightTab(btnhome);
            }
        });

        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragmentWithDelay(new MapFragment(), 1000);
                highlightTab(btnmap);
            }
        });

        btnuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragmentWithDelay(new Profile(),1000);  // Load Profile fragment when clicked
            }
        });

        btnmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragmentWithDelay(new openmic(),1000);
                highlightTab(btnmsg);
            }
        });



        // Set up navigation item selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return handleNavigationItemSelected(item);
            }
        });
    }
    private void highlightTab(ImageButton selectedButton) {
        // Reset all tabs to default
        resetTabs();

        // Highlight the selected tab
        selectedButton.setBackgroundColor(Color.parseColor("#ADD8E6")); // Light Blue
        selectedButton.setColorFilter(ContextCompat.getColor(this, R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
        selectedButton.setSelected(true);
    }

    private void resetTabs() {
        btnhome.setBackgroundColor(Color.TRANSPARENT);
        btnmap.setBackgroundColor(Color.TRANSPARENT);
        btnmsg.setBackgroundColor(Color.TRANSPARENT);

        btnhome.clearColorFilter();
        btnmap.clearColorFilter();
        btnmsg.clearColorFilter();

        btnhome.setSelected(false);
        btnmap.setSelected(false);
        btnmsg.setSelected(false);
    }
    // Method to load a fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        // Avoid reloading the same fragment
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // Method to load a fragment with delay
    private void loadFragmentWithDelay(Fragment fragment, int delayMillis) {
        new android.os.Handler().postDelayed(() -> {
            if (!isDestroyed()) { // Ensure activity is still running
                loadFragment(fragment);
            }
        }, delayMillis);
    }

    // Handle navigation drawer item selection
    private boolean handleNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ho) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()) // Ensure HomeFragment() is valid
                    .commit();
        } else if (item.getItemId() == R.id.in) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new About()) // Ensure About() is valid
                    .commit();
        }
        else if (item.getItemId() == R.id.we) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new fragment_weather()) // Ensure About() is valid
                    .commit();
        }
        else if (item.getItemId() == R.id.lg) {
            mGoogleSignInClient.signOut();
            fAuth.signOut();
            Intent intent = new Intent(home.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            return false;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
