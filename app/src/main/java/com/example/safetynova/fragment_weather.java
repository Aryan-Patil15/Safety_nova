package com.example.safetynova;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class fragment_weather extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private TextView weatherTextView, locationTextView, windTextView, humidityTextView, precipitationTextView;

    private static final String API_KEY = "6a82827c9ebd041d71c65111a2452ad7";
    private static final String TAG = "WeatherFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        weatherTextView = view.findViewById(R.id.weatherTextView);
        locationTextView = view.findViewById(R.id.locationTextView);
        windTextView = view.findViewById(R.id.windTextView);
        humidityTextView = view.findViewById(R.id.humidityTextView);
        precipitationTextView = view.findViewById(R.id.precipitationTextView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) return; // Prevent null reference issues

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    getLocationName(latitude, longitude);
                    fetchWeather(latitude, longitude);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void getLocationName(double latitude, double longitude) {
        if (!isAdded()) return; // Ensure fragment is still attached

        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locationName = address.getLocality();
                if (locationName == null) {
                    locationName = address.getCountryName();
                }
                locationTextView.setText("\uD83D\uDCCD Current Location: " + locationName);
            } else {
                locationTextView.setText("Unable to retrieve location name.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            locationTextView.setText("Error retrieving location name.");
        }
    }

    private void fetchWeather(double latitude, double longitude) {
        if (!isAdded()) return; // Ensure fragment is still attached

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + API_KEY;

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("main")) {
                                JSONObject main = response.getJSONObject("main");
                                double temperature = main.getDouble("temp");
                                int humidity = main.getInt("humidity");
                                String weatherInfo = "\uD83C\uDF21\uFE0F Temperature: " + temperature + "°C";
                                weatherTextView.setText(weatherInfo);
                                humidityTextView.setText("\uD83D\uDCA7 Humidity: " + humidity + "%");

                                if (response.has("wind")) {
                                    JSONObject wind = response.getJSONObject("wind");
                                    double windSpeed = wind.getDouble("speed");
                                    windTextView.setText("\uD83C\uDF43 Wind Speed: " + windSpeed + " m/s");
                                } else {
                                    windTextView.setText("\uD83C\uDF43 Wind Speed: Data not available");
                                }

                                String precipitationInfo = "\uD83C\uDF27\uFE0F Precipitation: No data available";
                                if (response.has("rain")) {
                                    JSONObject rain = response.getJSONObject("rain");
                                    if (rain.has("1h")) {
                                        double precipitation = rain.getDouble("1h");
                                        precipitationInfo = "\uD83C\uDF27\uFE0F Precipitation: " + precipitation + " mm";
                                    }
                                }
                                precipitationTextView.setText(precipitationInfo);
                            } else {
                                Toast.makeText(getContext(), "Invalid weather data received.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing weather data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching weather data: " + error.getMessage());
                        Toast.makeText(getContext(), "Error fetching weather data.", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
