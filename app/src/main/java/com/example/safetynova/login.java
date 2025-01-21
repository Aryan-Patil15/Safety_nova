package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In
    private static final String TAG = "GoogleLogin";

    private EditText emailPhoneInput, passwordInput;
    private Button loginButton;
    private ImageView facebookIcon, googleIcon, linkedinIcon;
    private TextView t;

    private FirebaseAuth fAuth;
    private GoogleSignInClient googleSignInClient;

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

        fAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("193867853435-l3fh0m7racs996uekvj32ulbmum5chpn.apps.googleusercontent.com") // Replace with your web client ID
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set social media icons click behavior
        facebookIcon.setOnClickListener(v -> Toast.makeText(login.this, "Login with Facebook", Toast.LENGTH_SHORT).show());

        googleIcon.setOnClickListener(v -> signInWithGoogle());

        linkedinIcon.setOnClickListener(v -> Toast.makeText(login.this, "Login with LinkedIn", Toast.LENGTH_SHORT).show());

        // Sign-up redirection
        t.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
        });

        // Set login button behavior
        loginButton.setOnClickListener(view -> {
            // Get text from EditText fields
            String email = emailPhoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if ((password.length() < 6) || (password.length() > 100)) {
                Toast.makeText(login.this, "Password must have at least 6 characters and a maximum of 10 characters", Toast.LENGTH_SHORT).show();
            } else {
                login(email, password);
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken(),account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken,GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = fAuth.getCurrentUser();
                            // Check if the user is new
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                googleSignInClient.signOut();
                                user.delete();
                                // Handle new user (e.g., display welcome message)
                                Toast.makeText(this, "User Not found, Please Register", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle existing user (e.g., directly navigate to main activity)
                                Toast.makeText(this, "Welcome Back "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                navigateToHome();
                            }
                            // ...
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                            // ...
                        }
                });
    }

    private void navigateToHome() {
        startActivity(new Intent(this, home.class));
        finish();
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
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(login.this, "Phone verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

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
