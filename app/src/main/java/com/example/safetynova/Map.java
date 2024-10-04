package com.example.safetynova;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.*;
public class Map extends AppCompatActivity {
    SupportMapFragment smf;
    FusedLocationProviderClient clent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        clent = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Map.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(Map.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        Task<Location> task = clent.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                smf.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady( GoogleMap googleMap) {
                        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Vedant"));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    }
                });
            }
        });
    }
}