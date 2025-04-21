package com.example.safetynova;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import android.text.TextWatcher;
import android.text.Editable;

import java.util.HashMap;
import java.util.Map;


public class signup extends AppCompatActivity {

    private EditText emailInput, phoneInput, passwordInput, confirmPasswordInput, dobInput, fullNameInput,otpInput;
    private Button signUpButton,verifyOtpButton;
    private ImageView googleSignUpButton;
    private TextView loginText;
    private boolean isPasswordVisible = false;
    private FirebaseAuth fAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore firebaseFirestore;
    private String VerificationId,fullName,email,phone,dob,password,confirmPassword;
    FirebaseUser user;

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
        // Initialize UI components
        otpInput = findViewById(R.id.otp_input);
        verifyOtpButton = findViewById(R.id.verify_otp_button);

        fAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("193867853435-l3fh0m7racs996uekvj32ulbmum5chpn.apps.googleusercontent.com") // Replace with actual Web Client ID
                .requestEmail()
                .requestProfile() // Request profile information
                .requestScopes(new Scope(Scopes.PROFILE)) // Request profile scope
                .requestScopes(new Scope(Scopes.PLUS_LOGIN)) // Request phone scope (deprecated)
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Set listeners for buttons and inputs
        dobInput.setOnClickListener(v -> showDatePickerDialog());
        signUpButton.setOnClickListener(v -> validateAndRegister());
        googleSignUpButton.setOnClickListener(v -> signInWithGoogle());
        loginText.setOnClickListener(v -> navigateToLogin());
        passwordInput.setOnTouchListener((v, event) -> togglePasswordVisibility(event));
        verifyOtpButton.setOnClickListener(v -> validate());

        // Confirm password error feedback
        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!confirmPasswordInput.getText().toString().equals(passwordInput.getText().toString())) {
                    confirmPasswordInput.setError("Passwords do not match");
                } else {
                    confirmPasswordInput.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(signup.this,
                (view, year1, monthOfYear, dayOfMonth) ->
                        dobInput.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1)),
                year, month, day);
        datePickerDialog.show();
    }

    private void validateAndRegister() {
        fullName = fullNameInput.getText().toString().trim();
        email = emailInput.getText().toString().trim();
        phone = phoneInput.getText().toString().trim();
        dob = dobInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        confirmPassword = confirmPasswordInput.getText().toString().trim();

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

        if (password.length() > 10) {
            passwordInput.setError("Password must be less than 10 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        addUser(fullName, email, phone, dob, password);
    }

    private void addUser(String fullName, String email, String phone, String dob, String password) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user = task.getResult().getUser();
                verifyPhoneNumber(phone);
            } else {
                Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyPhoneNumber(String phoneNumber) {
        String PhoneNumber = "+91" + phoneNumber; // Adjust for dynamic country codes
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth)
                .setPhoneNumber(PhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        linkPhoneCredentialToUser(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        user.delete();
                        Toast.makeText(signup.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        FrameLayout otp = findViewById(R.id.otp);
                        otp.setVisibility(View.VISIBLE);
                        otp.bringToFront();
                        VerificationId=verificationId;
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void validate() {
        String otp = otpInput.getText().toString().trim();
        if (otp.isEmpty()) {
            Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
        } else {
            verifyOtp(otp);
        }
    }

    private void verifyOtp(String otp) {
        if (VerificationId != null) {
            // Create PhoneAuthCredential with the verification ID and OTP
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, otp);
            // Link phone credential to the Firebase user
            linkPhoneCredentialToUser(credential);
        } else {
            Toast.makeText(this, "Verification ID is null", Toast.LENGTH_SHORT).show();
        }
    }
    private void linkPhoneCredentialToUser(PhoneAuthCredential credential) {
        if (user != null) {
            user.linkWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    submitDataToFirestore(fullName,email,phone,dob);
                } else {
                    Toast.makeText(this, "Error linking phone: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Firebase user is null", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                user = fAuth.getCurrentUser();
                    Toast.makeText(this, "Welcome " + (user != null ? user.getDisplayName() : ""), Toast.LENGTH_SHORT).show();
                mGoogleSignInClient.signOut();
                phone=String.valueOf(user.getPhoneNumber());
                
                submitDataToFirestore(user.getDisplayName(),user.getEmail(),phone,"N/A");
            } else {
                Toast.makeText(this, "Google Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, login.class));
    }
    private void navigateTomedical() {
        startActivity(new Intent(this, MedicalForm.class));
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
    private void submitDataToFirestore(String fullName, String email, String phone, String dob) {
        // Create a map of the data
        Map<String, Object> data = new HashMap<>();
        data.put("full_name", fullName);
        data.put("email",email); // Store age as an integer
        data.put("phone",phone);
        data.put("dob", dob);

        // Wrapping Medical_Form inside another map to nest it properly
        Map<String, Object> userData = new HashMap<>();
        userData.put("User_data", data);

        // Add data to Firestore
        firebaseFirestore.collection("User")
                .document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> showToast("Data submitted successfully"))
                .addOnFailureListener(e -> showToast("Error submitting data: " + e.getMessage()));
        Toast.makeText(this, "Phone number verified and linked successfully", Toast.LENGTH_SHORT).show();
        // Navigate to the home screen or next activity
        navigateTomedical();
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}