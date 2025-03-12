package com.example.feelsave;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Für das Standorttracking
public class LocationListener {

    private final Context context;
    private final Activity activity;
    private final ArrayList<String> locationsList;
    private final FireBaseHelper fireBaseHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public LocationListener(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.locationsList = new ArrayList<>();
        this.fireBaseHelper = new FireBaseHelper();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission") // Berechtigungen werden in einer anderen Klasse überprüft
    public void getLocation() {
        Log.d("LocationListener", "getLocation() wurde aufgerufen");

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(3000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    android.location.Location location = locationResult.getLastLocation();
                    handleLocationUpdate(location);
                }
            }
        };

        // Standortupdates anfordern
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Log.d("LocationListener", "Standort abrufen erfolgreich");
    }

    private void handleLocationUpdate(android.location.Location location) {
        // Standort anzeigen
        Toast.makeText(context, "Standort: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                locationsList.add(address);
                Log.d("LocationListener", "Neue Adresse: " + address + " | Bisherige Adressen: " + locationsList);

                // Standort in Firebase speichern
                fireBaseHelper.addLocation(address).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LocationListener", "Standort erfolgreich in Firebase gespeichert.");
                    } else {
                        Log.e("LocationListener", "Fehler beim Speichern des Standorts in Firebase.", task.getException());
                    }
                });
            } else {
                Log.w("LocationListener", "Keine Adresse für diesen Standort gefunden.");
            }
        } catch (Exception e) {
            Log.e("LocationListener", "Fehler beim Abrufen der Adresse", e);
        }
    }

    // Methode zum Stoppen der Standort-Updates
    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            locationCallback = null; // Optional: Rücksetzen, damit nicht versehentlich doppelt gestoppt wird
            Log.d("LocationListener", "Standort-Updates gestoppt");
        } else {
            Log.d("LocationListener", "locationCallback ist null – keine Updates zu stoppen");
        }
    }
}
