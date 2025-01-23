package com.example.safetynova;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TrustedContactsSelect extends AppCompatActivity {

    private ListView contactsListView;
    private EditText searchEditText;
    private Button selectButton;
    private SimpleCursorAdapter adapter;
    private SparseBooleanArray checkedStates = new SparseBooleanArray();
    private HashMap<String, String> selectedContacts = new HashMap<>();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts_select);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        // Initialize Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        contactsListView = findViewById(R.id.contactsListView);
        searchEditText = findViewById(R.id.searchEditText);
        selectButton = findViewById(R.id.selectButton);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            loadContacts();
        }

        selectButton.setOnClickListener(view -> {
            collectSelectedContacts();
            if (selectedContacts.isEmpty()) {
                showAlertDialog("No contacts selected.");
            } else {
                showSelectedContactsAlert();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                restoreCheckedStates();
            }
        });

        contactsListView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            @SuppressLint("Range") String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            boolean isChecked = contactsListView.isItemChecked(position);
            checkedStates.put(Integer.parseInt(contactId), isChecked);
        });
    }

    private void loadContacts() {
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 AND " + ContactsContract.Contacts.DISPLAY_NAME + " IS NOT NULL",
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            String[] fromColumns = {ContactsContract.Contacts.DISPLAY_NAME};
            int[] toViews = {android.R.id.text1};
            adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_multiple_choice,
                    cursor,
                    fromColumns,
                    toViews,
                    0
            );

            adapter.setFilterQueryProvider(constraint -> {
                String filter = constraint != null ? constraint.toString() : "";
                return getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI,
                        null,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 AND " +
                                ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?",
                        new String[]{"%" + filter + "%"},
                        ContactsContract.Contacts.DISPLAY_NAME + " ASC"
                );
            });

            contactsListView.setAdapter(adapter);
            contactsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
    }

    private void collectSelectedContacts() {
        selectedContacts.clear();
        Cursor cursor = adapter.getCursor();
        if (cursor != null) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                @SuppressLint("Range") String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                boolean isChecked = checkedStates.get(Integer.parseInt(contactId), false);
                if (isChecked) {
                    @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    @SuppressLint("Range") String contactNumber = getContactNumber(contactId);
                    selectedContacts.put(contactName, contactNumber);
                }
            }
        }
    }

    private String getContactNumber(String contactId) {
        Cursor phonesCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
        );

        String contactNumber = "";
        if (phonesCursor != null) {
            if (phonesCursor.moveToFirst()) {
                @SuppressLint("Range") String number = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactNumber = number;
            }
            phonesCursor.close();
        }
        return contactNumber;
    }

    private void restoreCheckedStates() {
        Cursor cursor = adapter.getCursor();
        if (cursor != null) {
            int position = 0;
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                boolean isChecked = checkedStates.get(Integer.parseInt(contactId), false);
                contactsListView.setItemChecked(position, isChecked);
                position++;
            }
        }
    }

    private void showSelectedContactsAlert() {
        StringBuilder message = new StringBuilder("Selected Contacts:\n\n");
        for (String name : selectedContacts.keySet()) {
            message.append(name).append(" - ").append(selectedContacts.get(name)).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Selection")
                .setMessage(message.toString())
                .setPositiveButton("OK", (dialog, which) -> submitDataToFirestore())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void submitDataToFirestore() {
        List<String> contactNumbers = new ArrayList<>(selectedContacts.values());

        // Update the TrustedContacts field in the Profile collection
        firebaseFirestore.collection("User")
                .document(user.getUid())
                .set(new HashMap<String, Object>() {{
                    put("TrustedContacts", contactNumbers);
                }}, SetOptions.merge())
                .addOnSuccessListener(aVoid -> navigateTologin())
                .addOnFailureListener(e -> showAlertDialog("Failed to save trusted contacts: " + e.getMessage()));
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
    private void navigateTologin() {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Contacts Selection")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else {
            showAlertDialog("Permission to read contacts denied.");
        }
    }
}
