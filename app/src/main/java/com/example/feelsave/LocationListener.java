package com.example.feelsave;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationListener implements android.location.LocationListener {

    private Context context;
    private Activity activity;
    private LocationManager locationManager;
    private ArrayList<String> locationsList;
    private FireBaseHelper fireBaseHelper;



    public LocationListener(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        this.locationsList = new ArrayList<>();
        this.fireBaseHelper = ObjectManager.getInstance(context).getFireBaseHelperInstance();

    }



    @SuppressLint("MissingPermission") //Permissions werden in einer anderen Klasse gecheckt.
    public void getLocation() {
        try {
            locationManager= (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE); //getAppContext kann nur in einer Activity benutzt werden
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
            Geocoder geocoder = new Geocoder(context, Locale.getDefault()); //GeoCoder übersetzt Koordinaten zu Adressen
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1); //Von hier nimmt er die Addressen
            String address = addresses.get(0).getAddressLine(0); //Greift auf die erste Adresse der Liste zu
            locationsList.add(address);
            Log.d("LocationClass", "Adresse: "+address + "Bisherige Adressen" + locationsList);
            fireBaseHelper.addLocationToDB(address);

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
