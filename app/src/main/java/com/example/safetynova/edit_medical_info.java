package com.example.safetynova;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class edit_medical_info extends AppCompatActivity {

    EditText medicalcodntextview,allergiestextview;
    Spinner genderspinner,blood_grpspinner;
    Button submit;
    FirebaseFirestore firestore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String[] genderOptions, blood_grpOptions;
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
        submit.setOnClickListener(v -> {});
        firestore.collection("User").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> medicalForm = (Map<String, Object>) documentSnapshot.get("Medical_Form");

                        if (medicalForm != null) {
                            String gender = (String) medicalForm.get("gender");
                            String blood_grp = (String) medicalForm.get("blood_group");
                            String allergies = (String) medicalForm.get("allergies");
                            String medicalcodn = (String) medicalForm.get("medical_condition");

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
    }
}