package com.example.safetynova;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class signup extends AppCompatActivity {

    private EditText emailInput, phoneInput, passwordInput, confirmPasswordInput, dobInput, fullNameInput;
    private Button signUpButton;
    private TextView loginText;
    boolean isPasswordVisible;
    String fullName, email, phone, dob, password, confirmPassword;
    FirebaseAuth fAuth;

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
        dobInput = findViewById(R.id.dob_input);  // Date of Birth Input
        signUpButton = findViewById(R.id.signup_button);
        loginText = findViewById(R.id.login);

        isPasswordVisible = false;

        fAuth = FirebaseAuth.getInstance();

        // Set Date of Birth field click listener
        dobInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set sign-up button click behavior
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = fullNameInput.getText().toString();
                email = emailInput.getText().toString();
                phone = phoneInput.getText().toString();
                dob = dobInput.getText().toString();  // Get date of birth input
                password = passwordInput.getText().toString();
                confirmPassword = confirmPasswordInput.getText().toString();

                if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!(password.length() == 6)) {
                    Toast.makeText(signup.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else if (!phone.matches("[6-9][0-9]{9}")) {
                    Toast.makeText(signup.this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    adduser();
                }
            }
        });

        // Navigate to Login activity
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, login.class);
                startActivity(intent);
            }
        });

        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Optional: Implement actions before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Dynamically fetch the password value
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                // Compare passwords and set error
                if (!confirmPassword.equals(password)) {
                    confirmPasswordInput.setError("Passwords do not match");
                } else {
                    confirmPasswordInput.setError(null); // Clear the error
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Optional: Implement actions after text is changed
            }
        });

        passwordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[2].getBounds().width())) {
                        // Save current padding
                        int paddingStart = passwordInput.getPaddingStart();
                        int paddingTop = passwordInput.getPaddingTop();
                        int paddingEnd = passwordInput.getPaddingEnd();
                        int paddingBottom = passwordInput.getPaddingBottom();

                        if (isPasswordVisible) {
                            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordInput.post(() -> {
                                passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visible_off, 0);
                            });
                        } else {
                            passwordInput.setTransformationMethod(null);
                            passwordInput.post(() -> {
                                passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visible, 0);
                            });
                        }

                        // Toggle visibility state
                        isPasswordVisible = !isPasswordVisible;

                        // Reapply padding and stabilize layout
                        passwordInput.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
                        passwordInput.setSelection(passwordInput.getText().length());
                        passwordInput.requestLayout();
                        passwordInput.invalidate();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    // Method to show Date Picker Dialog
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(signup.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set selected date to the dobInput field
                        dobInput.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void adduser() {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // User created successfully, now verify phone
                    verifyPhoneNumber(phone, task.getResult().getUser());
                } else {
                    Toast.makeText(signup.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyPhoneNumber(String phoneNumber, FirebaseUser user) {
        phoneNumber = formatPhoneNumber(phoneNumber);
        // Add the phone number with the country code
        phoneNumber = "+91" + phoneNumber; // Change country code as needed

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth)
                .setPhoneNumber(phoneNumber)  // Complete phone number with country code
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Automatically verify phone and link to email user
                        linkPhoneCredentialToUser(credential, user);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // Log error message for debugging
                        Toast.makeText(signup.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // OTP sent successfully, handle user input
                        Intent intent = new Intent(signup.this, VerifyOtpActivity.class);
                        intent.putExtra("verificationId", verificationId);
                        intent.putExtra("firebaseUser", user.getUid());
                        startActivity(intent);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private String formatPhoneNumber(String phone) {
        String digits = phone.replaceAll("\\D", "");

        return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
    }

    private void linkPhoneCredentialToUser(PhoneAuthCredential credential, FirebaseUser user) {
        user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Phone number linked successfully
                    Toast.makeText(signup.this, "Account created and phone linked!", Toast.LENGTH_SHORT).show();
                } else {
                    // Error linking phone number
                    Toast.makeText(signup.this, "Phone number link failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
