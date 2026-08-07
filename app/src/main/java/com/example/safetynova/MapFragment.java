package com.example.safetynova;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap googleMap;
    private ProgressDialog progressDialog;

    // ActivityResultLauncher for permission requests
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getUserLocation();
                } else {
                    Toast.makeText(getContextSafe(), "Permission denied. Please enable location access for this feature.", Toast.LENGTH_SHORT).show();
                }
            });

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap = gMap;
                if (getActivity() != null) {
                    checkAndRequestPermissions();
                }
            }
        });

        return view;
    }

    private void checkAndRequestPermissions() {
        if (!isLocationEnabled()) {
            promptEnableLocation();
            return;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            // Request the permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void promptEnableLocation() {
        Context context = getContextSafe();
        if (context == null) return;

        new AlertDialog.Builder(context)
                .setTitle("Enable Location Services")
                .setMessage("Location services are required for this feature. Please enable them in your settings.")
                .setPositiveButton("Enable", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);

                    // Recheck location settings when returning
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void getUserLocation() {
        Context context = getContextSafe();
        if (context == null || getActivity() == null) {
            Toast.makeText(context, "Unable to fetch location. Try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Location permission not granted.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading dialog
        progressDialog = ProgressDialog.show(context, "Fetching Location", "Please wait...", true);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            progressDialog.dismiss();
            if (location != null) {
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (googleMap != null) {
                    googleMap.clear(); // Clear any existing markers
                    googleMap.addMarker(new MarkerOptions().position(userLatLng).title("You are here"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                }
            } else {
                Toast.makeText(context, "Unable to fetch location. Try again later.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(context, "Failed to fetch location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isLocationEnabled() {
        Context context = getContextSafe();
        if (context == null) return false;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private Context getContextSafe() {
        return getContext() != null ? getContext() : requireActivity().getApplicationContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        new android.os.Handler().postDelayed(() -> {
            if (googleMap != null && isLocationEnabled()) {
                checkAndRequestPermissions();
            }
        }, 300); // 300ms delay for smooth loading
    }
}