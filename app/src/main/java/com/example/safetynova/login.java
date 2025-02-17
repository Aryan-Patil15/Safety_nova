package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleLogin";

    private EditText emailPhoneInput, passwordInput;
    private Button loginButton;
    private boolean isPasswordVisible = false;
    private ImageView facebookIcon, googleIcon, linkedinIcon;
    private TextView t, forgetpassword;

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
        forgetpassword = findViewById(R.id.forgetpassword);
        passwordInput.setOnTouchListener((v, event) -> togglePasswordVisibility(event));

        fAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("193867853435-l3fh0m7racs996uekvj32ulbmum5chpn.apps.googleusercontent.com")  // Replace with your web client ID
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Handle Google sign-in click
        googleIcon.setOnClickListener(v -> signInWithGoogle());

        // Sign-up redirection
        t.setOnClickListener(v -> startActivity(new Intent(login.this, signup.class)));

        // Forget password redirection
        forgetpassword.setOnClickListener(v -> startActivity(new Intent(login.this, Forgetpass.class)));

        // Facebook and LinkedIn icon click handling
        facebookIcon.setOnClickListener(v -> Toast.makeText(login.this, "Coming soon!!", Toast.LENGTH_SHORT).show());
        linkedinIcon.setOnClickListener(v -> Toast.makeText(login.this, "Coming soon!!", Toast.LENGTH_SHORT).show());

        // Login button click handling
        loginButton.setOnClickListener(view -> {
            String email = emailPhoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6 || password.length() > 100) {
                Toast.makeText(login.this, "Password must have at least 6 characters and a maximum of 100 characters", Toast.LENGTH_SHORT).show();
            } else {
                login(email, password);
            }
        });
    }


    private boolean togglePasswordVisibility(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[2].getBounds().width())) {
                if (isPasswordVisible) {
                    passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visible_off, 0);
                } else {
                    passwordInput.setTransformationMethod(null);
                    passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visible, 0);
                }
                isPasswordVisible = !isPasswordVisible;
                return true;
            }
        }
        return false;
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
                firebaseAuthWithGoogle(account.getIdToken(), account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign-in failed", e);
                Toast.makeText(this, ""+e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = fAuth.getCurrentUser();
                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                    googleSignInClient.signOut();
                    if (user != null) {
                        user.delete();
                        Toast.makeText(this, "User not found. Please register.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Welcome back " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    navigateToHome();
                }
            } else {
                Toast.makeText(this, "Google login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        startActivity(new Intent(this, home.class));
        finish();
    }

    private void login(String input, String password) {
        if (input.contains("@")) {
            fAuth.signInWithEmailAndPassword(input, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Login successful with email", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                } else {
                    handleFirebaseAuthException(task.getException());
                }
            });
        } else {
            verifyPhoneNumber(input);
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
                navigateToHome();
            } else {
                handleFirebaseAuthException(task.getException());
            }
        });
    }

    private void handleFirebaseAuthException(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, "Invalid user account.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
