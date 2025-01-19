package com.example.safetynova;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;

public class TrustedContactsSelect extends AppCompatActivity {

    private ListView contactsListView;
    private EditText searchEditText;
    private Button selectButton;
    private SimpleCursorAdapter adapter;
    private SparseBooleanArray checkedStates = new SparseBooleanArray();
    private HashMap<String, String> selectedContacts = new HashMap<>();
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts_select);

        contactsListView = findViewById(R.id.contactsListView);
        searchEditText = findViewById(R.id.searchEditText);
        selectButton = findViewById(R.id.selectButton);
        fragmentContainer = findViewById(R.id.fragment_container);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            loadContacts();
        }

        selectButton.setOnClickListener(view -> {
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

            if (selectedContacts.isEmpty()) {
                showAlertDialog("No contacts selected.", false);
            } else {
                StringBuilder contactsMessage = new StringBuilder();
                for (HashMap.Entry<String, String> entry : selectedContacts.entrySet()) {
                    contactsMessage.append(entry.getKey()).append(" (").append(entry.getValue()).append(")\n");
                }
                showAlertDialog("Selected Contacts:\n" + contactsMessage, true);
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

    private void showAlertDialog(String message, boolean proceedToFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contacts Selection")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (proceedToFragment) {
                        openLiveLocationSharingFragment();
                    }
                })
                .show();
    }

    private void openLiveLocationSharingFragment() {
        LiveLocationSharing fragment = new LiveLocationSharing();

        // Pass selectedContacts to the fragment using a Bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable("trustedContacts", selectedContacts);
        fragment.setArguments(bundle);

        // Make the fragment container visible and add the fragment
        fragmentContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else {
            showAlertDialog("Permission to read contacts denied.", false);
        }
    }
}
