package com.example.safetynova;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LiveLocationSharing extends Fragment {

    private TextView contactListTextView;
    private Button sendLocationButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private HashMap<String, String> trustedContacts = new HashMap<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1002;
    private String currentLocationLink;
    private SmsReceiver smsReceiver;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Retrieve trusted contacts from the arguments
        if (getArguments() != null) {
            trustedContacts = (HashMap<String, String>) getArguments().getSerializable("trustedContacts");
        }

        // Register the SMS receiver to listen for SMS sent results
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("SMS_SENT");
        requireContext().registerReceiver(smsReceiver, filter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_location_sharing, container, false);
        contactListTextView = view.findViewById(R.id.contactListTextView);
        sendLocationButton = view.findViewById(R.id.sendLocationButton);

        // Display the contacts
        displayContacts();

        // Initialize location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationTracking();
        }

        sendLocationButton.setOnClickListener(v -> {
            if (currentLocationLink != null) {
                // Save location to Firestore and share
                saveLocationToFirestore(currentLocationLink);
            } else {
                Toast.makeText(requireContext(), "Fetching location... Please wait.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void displayContacts() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Access the 'User' collection and get the document for the current user
        firestore.collection("User").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve the TrustedContacts and TrustedNames fields
                            List<String> trustedContacts = (List<String>) document.get("TrustedContacts");
                            List<String> trustedNames = (List<String>) document.get("TrustedNames");

                            if (trustedContacts == null || trustedNames == null || trustedContacts.isEmpty() || trustedNames.isEmpty()) {
                                contactListTextView.setText("No trusted contacts available.");
                                return;
                            }

                            // Build the contact list string
                            StringBuilder contactList = new StringBuilder("Trusted Contacts:\n");
                            for (int i = 0; i < trustedNames.size() && i < trustedContacts.size(); i++) {
                                contactList.append(trustedNames.get(i))
                                        .append(" - ")
                                        .append(trustedContacts.get(i))
                                        .append("\n");
                            }

                            contactListTextView.setText(contactList.toString());
                        } else {
                            contactListTextView.setText("No trusted contacts available.");
                        }
                    } else {
                        contactListTextView.setText("Error retrieving contacts.");
                    }
                })
                .addOnFailureListener(e -> {
                    contactListTextView.setText("Failed to fetch trusted contacts: " + e.getMessage());
                });
    }


    private void startLocationTracking() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);  // 5 seconds interval
        locationRequest.setFastestInterval(2000); // 2 seconds fastest interval

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            currentLocationLink = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                        }
                    }
                }
            }, requireActivity().getMainLooper());
        }
    }

    private void saveLocationToFirestore(String locationLink) {
        // Generate a unique document ID
        String uniqueDocumentId = UUID.randomUUID().toString();

        HashMap<String, Object> locationData = new HashMap<>();
        locationData.put("LiveLocation", locationLink);

        DocumentReference documentReference = firebaseFirestore.collection("Profile").document(uniqueDocumentId);
        documentReference.set(locationData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Share location with contacts after saving
                    shareLocationWithContacts(locationLink);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void shareLocationWithContacts(String locationLink) {
        if (trustedContacts.isEmpty()) {
            Toast.makeText(requireContext(), "No trusted contacts to share location.", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        for (HashMap.Entry<String, String> entry : trustedContacts.entrySet()) {
            String name = entry.getKey();
            String phoneNumber = entry.getValue();
            String message = "Hi " + name + ", I am continuously sharing my real-time location: " + locationLink;

            // Create a PendingIntent for SMS delivery confirmation
            Intent intent = new Intent("SMS_SENT");
            PendingIntent sentPI = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            try {
                smsManager.sendTextMessage(phoneNumber, null, message, sentPI, null);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Toast.makeText(requireContext(), "Location sent to trusted contacts.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking();
        } else if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Send the location message after the permission is granted
                if (currentLocationLink != null) {
                    shareLocationWithContacts(currentLocationLink);
                }
            } else {
                Toast.makeText(requireContext(), "SMS permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Location permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the SMS receiver to avoid memory leaks
        requireContext().unregisterReceiver(smsReceiver);
    }


    // BroadcastReceiver to handle SMS delivery result
    public class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check the result of the SMS sending operation
            switch (getResultCode()) {
                case android.app.Activity.RESULT_OK:
                    Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context, "Generic failure in sending SMS.", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "No service available.", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context, "Null PDU error.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
