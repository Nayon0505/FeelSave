package com.example.feelsave;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHandler {

    private Context context;
    private Activity activity;


    public PermissionHandler(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        checkAllPermissions();
    }

    private void checkLocationPermisson(){       //Permissions für den Zugriff auf den Standort werden angefragt
        if (ContextCompat.checkSelfPermission(activity,  android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
    }

    private  /*boolean*/ void checkSMSPermission(/*String permission*/) {
        if (ContextCompat.checkSelfPermission(activity,  Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{
                    Manifest.permission.SEND_SMS
            },101);
        }
       /* int check = ContextCompat.checkSelfPermission(context,permission);
        return (check == PackageManager.PERMISSION_GRANTED);*/
    }
    //Villeicht überprüfen ob die die gleiche checkform nutzen kann
    private void checkContactsPermission(){
        if (ContextCompat.checkSelfPermission(activity,  Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{
                    Manifest.permission.SEND_SMS
            },102);
        }
    }

    public void checkAllPermissions(){
        checkLocationPermisson();
        checkSMSPermission();
        checkContactsPermission();
    }
}
