package com.example.safetynova;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class TrustedContactsSelect extends AppCompatActivity {

    private ListView contactsListView;
    private Button selectButton;
    private ArrayList<String> selectedContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts_select);

        contactsListView = findViewById(R.id.contactsListView);
        selectButton = findViewById(R.id.selectButton);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            loadContacts();
        }

        selectButton.setOnClickListener(view -> {
            selectedContacts.clear();
            for (int i = 0; i < contactsListView.getCount(); i++) {
                if (contactsListView.isItemChecked(i)) {
                    Cursor cursor = (Cursor) contactsListView.getItemAtPosition(i);
                    @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    selectedContacts.add(contactName);
                }
            }

            if (selectedContacts.isEmpty()) {
                showAlertDialog("No contacts selected.");
            } else {
                StringBuilder contactsMessage = new StringBuilder();
                for (String contact : selectedContacts) {
                    contactsMessage.append(contact).append("\n");
                }
                showAlertDialog("Selected Contacts:\n" + contactsMessage);
            }
        });
    }

    private void loadContacts() {
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 AND " + ContactsContract.Contacts.DISPLAY_NAME + " IS NOT NULL",
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC" // Sorting contacts alphabetically
        );

        if (cursor != null) {
            String[] fromColumns = {ContactsContract.Contacts.DISPLAY_NAME};
            int[] toViews = {android.R.id.text1};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_multiple_choice,
                    cursor,
                    fromColumns,
                    toViews,
                    0
            );

            contactsListView.setAdapter(adapter);
            contactsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contacts Selection")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Action for OK button
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Action for Cancel button
                    dialog.dismiss();
                })
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
