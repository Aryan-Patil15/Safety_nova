package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class edit_medical_info extends AppCompatActivity {

    EditText medicalcodntextview,allergiestextview;
    Spinner genderspinner,blood_grpspinner;
    Button submit;
    FirebaseFirestore firestore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String gender,blood_grp,allergies,medicalcodn;
    String[] genderOptions, blood_grpOptions;
    DocumentReference docref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_medical_info);
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        String userId = user.getUid();

        medicalcodntextview=findViewById(R.id.medical_condition_input);
        allergiestextview=findViewById(R.id.allergies_input);
        genderspinner=findViewById(R.id.gender_spinner);
        blood_grpspinner=findViewById(R.id.blood_group_input);
        submit=findViewById(R.id.submit_button);

        genderOptions = getResources().getStringArray(R.array.gender_options);
        blood_grpOptions = getResources().getStringArray(R.array.Blood_Group);
        docref=firestore.collection("User").document(userId);
        docref.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> medicalForm = (Map<String, Object>) documentSnapshot.get("Medical_Form");

                        if (medicalForm != null) {
                            gender = (String) medicalForm.get("gender");
                            blood_grp = (String) medicalForm.get("blood_group");
                            allergies = (String) medicalForm.get("allergies");
                            medicalcodn = (String) medicalForm.get("medical_condition");

                            medicalcodntextview.setText(medicalcodn!= null ? medicalcodn : "N/A");
                            allergiestextview.setText(allergies!= null ? allergies : "N/A");
                            int index = -1; // Default to -1 (not found)
                            // 1. Get the string array from strings.xml
                            genderOptions:
                            for (int i = 0; i < genderOptions.length; i++) {
                                if (genderOptions[i].equals(gender)) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index != -1) {
                                genderspinner.setSelection(index);
                                index=-1;
                            }
                            else {
                                genderspinner.setSelection(0);
                                index=-1;
                            }
                            for (int i = 0; i < blood_grpOptions.length; i++) {
                                if (blood_grpOptions[i].equals(gender)) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index != -1) {
                                blood_grpspinner.setSelection(index);
                            }
                            else {
                                blood_grpspinner.setSelection(0);
                            }
                        }
                    }
                });
        submit.setOnClickListener(view -> {
            String temp1 = gender;
            String temp2 = blood_grp;
            String temp3 = allergies;
            String temp4 = medicalcodn;

            if (genderspinner.getSelectedItem().toString().isEmpty()) {
                Toast.makeText(this, "Please select a Gender", Toast.LENGTH_SHORT).show();
                return;
            }
            if (blood_grpspinner.getSelectedItem().toString().isEmpty()) {
                Toast.makeText(this, "Please Select a BloodGroup", Toast.LENGTH_SHORT).show();
                return;
            }

            gender = genderspinner.getSelectedItem().toString();
            blood_grp = blood_grpspinner.getSelectedItem().toString();
            allergies = TextUtils.isEmpty(allergiestextview.getText().toString())? "None" : allergiestextview.getText().toString();
            medicalcodn = TextUtils.isEmpty(medicalcodntextview.getText().toString())? "None" : medicalcodntextview.getText().toString();

            Map<String, Object> updates = new HashMap<>();
            if (!gender.equals(temp1)) {
                updates.put("Medical_Form.gender", gender); // Using dot notation
            }
            if (!blood_grp.equals(temp2)) {
                updates.put("Medical_Form.blood_group", blood_grp);
            }
            if (!allergies.equals(temp3)) {
                updates.put("Medical_Form.allergies", allergies);
            }
            if (!medicalcodn.equals(temp4)) {
                updates.put("Medical_Form.medical_condition", medicalcodn);
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
            else
            {
                Intent intent = new Intent(this, home.class);
                startActivity(intent);
                finish();
            }
        });
    }
}