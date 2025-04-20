package com.example.safetynova;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiveLocationSharing extends Fragment {
    private TextView contactListTextView;
    private Button sendLocationButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final HashMap<String, String> trustedContacts = new HashMap<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1002;
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1003;
    private GeoPoint currentGeoPoint;
    private SmsReceiver smsReceiver;
    private FirebaseFirestore firebaseFirestore;
    private LocationCallback locationCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("SMS_SENT");
        requireContext().registerReceiver(smsReceiver, filter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_location_sharing, container, false);
        contactListTextView = view.findViewById(R.id.contactListTextView);
        sendLocationButton = view.findViewById(R.id.sendLocationButton);
        displayContacts();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestBackgroundLocationPermission();
            startLocationTracking();
        }

        sendLocationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            } else {
                shareLocationIfAvailable();
            }
        });
        return view;
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

                            StringBuilder contactList = new StringBuilder();
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
        // Create a LocationRequest with updates every 2 seconds and a maximum delay of 5 seconds.
        LocationRequest locationRequest = new LocationRequest.Builder(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(5000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    currentGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    // Continuously update Firestore with the new location.
                    updateLocationToFirestore(currentGeoPoint);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, requireActivity().getMainLooper());
        }
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Background Location Permission Needed")
                    .setMessage("This app requires background location access to share your live location continuously.")
                    .setPositiveButton("Grant Permission", (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    private void shareLocationIfAvailable() {
        if (currentGeoPoint != null) {
            saveLocationToFirestore(currentGeoPoint);
        } else {
            Toast.makeText(requireContext(), "Fetching location... Please wait.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method is triggered by the button click and updates Firestore
     * then shares the dynamic link with trusted contacts.
     */
    private void saveLocationToFirestore(GeoPoint geoPoint) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Object> locationData = new HashMap<>();
        locationData.put("LiveLocation", geoPoint);
        firebaseFirestore.collection("User").document(currentUserId)
                .set(locationData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> shareDynamicLinkWithContacts("https://livelocationsafetynova.netlify.app/?userId=" + currentUserId))
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to save location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * This helper method continuously updates Firestore with the new location,
     * without sending SMS dynamic links every time.
     */
    private void updateLocationToFirestore(GeoPoint geoPoint) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Object> locationData = new HashMap<>();
        locationData.put("LiveLocation", geoPoint);
        firebaseFirestore.collection("User").document(currentUserId)
                .set(locationData, SetOptions.merge())
                .addOnFailureListener(e -> {
                    // Optional: Handle the failure (for example, log the error).
                });
    }

    private void shareDynamicLinkWithContacts(String dynamicLink) {
        if (trustedContacts.isEmpty()) {
            Toast.makeText(requireContext(), "No trusted contacts to share location.", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        for (HashMap.Entry<String, String> entry : trustedContacts.entrySet()) {
            ArrayList<String> messageParts = smsManager.divideMessage("Hi " + entry.getKey() + ", track my live location: " + dynamicLink);
            smsManager.sendMultipartTextMessage(entry.getValue(), null, messageParts, null, null);
        }
        // Update Firestore with trackingStartTime so that the timer starts on the web page.
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Object> startTimeData = new HashMap<>();
        startTimeData.put("trackingStartTime", Timestamp.now());
        firebaseFirestore.collection("User").document(currentUserId)
                .set(startTimeData, SetOptions.merge());

        Toast.makeText(requireContext(), "Live Tracking link sent to trusted contacts.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireContext().unregisterReceiver(smsReceiver);
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    public class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "SMS status updated.", Toast.LENGTH_SHORT).show();
        }
    }
}