package com.example.safetynova;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private EditText Username, PhoneNumber, Age, DOB;
    String fullname,phno,dob,age;
    private Button submit;
    private FirebaseFirestore firestore;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    DocumentReference docref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        String userId = user.getUid();

        Username=findViewById(R.id.full_name_input);
        PhoneNumber=findViewById(R.id.phone_input);
        Age=findViewById(R.id.age_input);
        DOB=findViewById(R.id.dob_input);
        submit=findViewById(R.id.submit_button);

        DOB.setOnClickListener(v -> showDatePickerDialog());
        submit.setOnClickListener(v -> {});
        docref=firestore.collection("User").document(userId);
        docref.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userD = (Map<String, Object>) documentSnapshot.get("User_data");
                        Map<String, Object> medicalForm = (Map<String, Object>) documentSnapshot.get("Medical_Form");
                        if (userD != null) {
                             fullname = (String) userD.get("full_name");
                             phno = (String) userD.get("phone");
                             dob = (String) userD.get("dob");
                             age = String.valueOf(medicalForm.get("age"));

                            Username.setText(fullname!= null ? fullname : "N/A");
                            PhoneNumber.setText(phno!= null ? phno : "N/A");
                            Age.setText(age!= null ? age : "N/A");
                            DOB.setText(dob != null ? dob : "N/A");
                        }
                    }
                });
        submit.setOnClickListener(view -> {
            String temp1 = fullname;
            String temp2 = phno;
            String temp3 = age;
            String temp4 = dob;

            if (Username.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter a Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PhoneNumber.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter a PhoneNumber", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Age.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter an Age", Toast.LENGTH_SHORT).show();
                return;
            }
            if (DOB.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter a Date of Birth", Toast.LENGTH_SHORT).show();
                return;
            }

            fullname = Username.getText().toString();
            phno = PhoneNumber.getText().toString();
            age = Age.getText().toString();
            dob = DOB.getText().toString();

            Map<String, Object> updates = new HashMap<>();
            if (!fullname.equals(temp1)) {
                updates.put("User_data.full_name", fullname); // Using dot notation
            }
            if (!phno.equals(temp2)) {
                updates.put("User_data.phone", phno);
            }
            if (!age.equals(temp3)) {
                updates.put("Medical_Form.age", age);
            }
            if (!dob.equals(temp4)) {
                updates.put("User_data.dob", dob);
            }

            if (!updates.isEmpty()) { // Only update if there are changes
                docref.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, home.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            }
            else {
                Intent intent = new Intent(this, home.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        String[] dateParts = DOB.getText().toString().split("/"); // Assuming format is "dd/MM/yyyy"
        if (dateParts.length == 3) {
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Months are 0-based in Calendar
            int year = Integer.parseInt(dateParts[2]);
            calendar.set(year, month, day);
        }
        else {
        // Set a custom default date if no previous DOB exists
        calendar.set(2000, Calendar.JANUARY, 1);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) ->
                        DOB.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1)),
                year, month, day);
        datePickerDialog.show();
    }
}