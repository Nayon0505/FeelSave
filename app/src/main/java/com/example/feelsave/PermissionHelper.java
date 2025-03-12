package com.example.feelsave;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

// Fragt die Berechtigungen ab
public class PermissionHelper {
    private static final int FOREGROUND_PERMISSION_REQUEST_CODE = 100;
    private static final int BACKGROUND_PERMISSION_REQUEST_CODE = 101;

    private final Activity activity;

    // Vordergrund-Berechtigungen
    private final String[] foregroundPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS
    };

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    // 1. Vordergrundberechtigungen anfragen
    public void checkAndRequestForegroundPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String permission : foregroundPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    activity,
                    listPermissionsNeeded.toArray(new String[0]),
                    FOREGROUND_PERMISSION_REQUEST_CODE
            );
        } else {
            Log.d("PermissionHelper", "Alle Vordergrundberechtigungen sind bereits erteilt.");
            // Nachdem Vordergrundberechtigungen erteilt wurden, Hintergrundberechtigung anfragen
            checkAndRequestBackgroundPermission();
        }
    }

    // 2. Hintergrundberechtigung anfragen
    public void checkAndRequestBackgroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        BACKGROUND_PERMISSION_REQUEST_CODE
                );
            } else {
                Log.d("PermissionHelper", "Hintergrundberechtigung ist bereits erteilt.");
            }
        }
    }

    // Ergebnisauswertung der Anfragen
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == FOREGROUND_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Log.d("PermissionHelper", "Alle Vordergrundberechtigungen wurden erteilt.");
                // Nach erfolgreicher Anfrage der Vordergrundberechtigungen, Hintergrundberechtigung anfragen
                checkAndRequestBackgroundPermission();
            } else {
                Log.e("PermissionHelper", "Mindestens eine Vordergrundberechtigung wurde verweigert.");
            }
        } else if (requestCode == BACKGROUND_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionHelper", "Hintergrundberechtigung wurde erteilt.");
            } else {
                Log.e("PermissionHelper", "Hintergrundberechtigung wurde verweigert.");
            }
        }
    }
}
