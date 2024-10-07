package com.example.feelsave;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class LocationListener implements android.location.LocationListener {

    private Context context;
    private Activity activity;
    LocationManager locationManager;

    public LocationListener(Context context, Activity activity){
        this.context = context;
        this.activity = activity;

    }

    public void checkLocationPermisson(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,  Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
    }

    public void getLocation() {
        try {
            locationManager= (LocationManager)activity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE); //getAppContext kann nur in einer Activity benutzt werden
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,this); //this = LocationListener vlt muss der in die MainActivity
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        Toast.makeText(context,""+location.getLatitude()+""+location.getLongitude(),Toast.LENGTH_SHORT).show();

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            Log.d("LocationClass", "Adresse: "+address);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(@NonNull List<android.location.Location> locations) {
        android.location.LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        android.location.LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        android.location.LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        android.location.LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        android.location.LocationListener.super.onProviderDisabled(provider);
    }
}
