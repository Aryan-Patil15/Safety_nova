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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;

public class LiveLocationSharing extends Fragment {

    private TextView contactListTextView;
    private Button sendLocationButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private HashMap<String, String> trustedContacts = new HashMap<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1002;
    private GeoPoint currentGeoPoint;
    private SmsReceiver smsReceiver;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

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
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // Request SMS permission
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            } else {
                // Proceed to share location if permission is granted
                shareLocationIfAvailable();
            }
        });

        return view;
    }

    private void shareLocationIfAvailable() {
        if (currentGeoPoint != null) {
            saveLocationToFirestore(currentGeoPoint);
        } else {
            Toast.makeText(requireContext(), "Fetching location... Please wait.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayContacts() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("User").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> trustedContactsList = (List<String>) document.get("TrustedContacts");
                            List<String> trustedNamesList = (List<String>) document.get("TrustedNames");

                            if (trustedContactsList == null || trustedNamesList == null || trustedContactsList.isEmpty() || trustedNamesList.isEmpty()) {
                                contactListTextView.setText("No trusted contacts available.");
                                return;
                            }

                            trustedContacts.clear();
                            for (int i = 0; i < trustedNamesList.size() && i < trustedContactsList.size(); i++) {
                                trustedContacts.put(trustedNamesList.get(i), trustedContactsList.get(i));
                            }

                            StringBuilder contactList = new StringBuilder("Trusted Contacts:\n");
                            for (int i = 0; i < trustedNamesList.size() && i < trustedContactsList.size(); i++) {
                                contactList.append(trustedNamesList.get(i))
                                        .append(" - ")
                                        .append(trustedContactsList.get(i))
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
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            currentGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        }
                    }
                }
            }, requireActivity().getMainLooper());
        }
    }

    private void saveLocationToFirestore(GeoPoint geoPoint) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        HashMap<String, Object> locationData = new HashMap<>();
        locationData.put("LiveLocation", geoPoint);

        DocumentReference documentReference = firebaseFirestore.collection("User").document(currentUserId);
        documentReference.set(locationData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Generate dynamic link after saving location
                    String dynamicLink = "https://livelocationsafetynova.netlify.app/?userId=" + currentUserId;
                    shareDynamicLinkWithContacts(dynamicLink);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void shareDynamicLinkWithContacts(String dynamicLink) {
        if (trustedContacts.isEmpty()) {
            Toast.makeText(requireContext(), "No trusted contacts to share location.", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        for (HashMap.Entry<String, String> entry : trustedContacts.entrySet()) {
            String name = entry.getKey();
            String phoneNumber = entry.getValue();
            String message = "Hi " + name + ", track my live location using this link: " + dynamicLink;

            try {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(requireContext(), "Dynamic link sent to trusted contacts.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking();
        } else if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareLocationIfAvailable();
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
        requireContext().unregisterReceiver(smsReceiver);
    }

    public class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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
