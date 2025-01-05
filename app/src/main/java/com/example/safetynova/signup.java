package com.example.safetynova;

import android.app.DatePickerDialog;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Calendar;

public class signup extends AppCompatActivity {

    private EditText fullNameInput, emailInput, phoneInput, passwordInput, confirmPasswordInput, dobInput;
    private Button signUpButton;
    private ImageView googleSignUpButton;
    private TextView loginText;
    private boolean isPasswordVisible = false;

    private FirebaseAuth fAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        fullNameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        dobInput = findViewById(R.id.dob_input);
        signUpButton = findViewById(R.id.signup_button);
        googleSignUpButton = findViewById(R.id.google_icon);
        loginText = findViewById(R.id.login);

        // Initialize FirebaseAuth
        fAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your Web Client ID
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set listeners
        dobInput.setOnClickListener(v -> showDatePickerDialog());
        signUpButton.setOnClickListener(v -> validateAndRegister());
        googleSignUpButton.setOnClickListener(v -> signInWithGoogle());
        loginText.setOnClickListener(v -> navigateToLogin());

        // Password visibility toggle
        passwordInput.setOnTouchListener((v, event) -> togglePasswordVisibility(event));
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(signup.this,
                (view, year1, monthOfYear, dayOfMonth) -> dobInput.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1)),
                year, month, day);
        datePickerDialog.show();
    }

    private void validateAndRegister() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String dob = dobInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || dob.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        registerUser(email, password);
    }

    private void registerUser(String email, String password) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                navigateToLogin();
            } else {
                Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
            }
    );

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
            }
        } catch (ApiException e) {
            Log.e("GoogleSignIn", "Google sign-in failed", e);
            Toast.makeText(signup.this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account)
            {
                Toast.makeText(signup.this, "Successful", Toast.LENGTH_SHORT).show();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = fAuth.getCurrentUser();
                Toast.makeText(signup.this, "Welcome " + (user != null ? user.getDisplayName() : ""), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, login.class)); // Replace with your main activity
                finish();
            } else {
                Toast.makeText(signup.this, "Google Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(signup.this, login.class)); // Replace with your login activity
    }

    private boolean togglePasswordVisibility(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[2].getBounds().width())) {
                isPasswordVisible = !isPasswordVisible;
                passwordInput.setTransformationMethod(isPasswordVisible ? null : new PasswordTransformationMethod());
                return true;
            }
        }
        return false;
    }
}
