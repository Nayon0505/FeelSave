package com.example.feelsave;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Von hier aus Startet die App und hier geschieht alles

public class MainActivity extends AppCompatActivity {

    private ButtonHandler buttonHandler;
    private SafeMode safeMode;
    private CountDown countDown;
    private LocationListener locationListener;
    private FireBaseHelper fireBaseHelper;
    private TextView emergencyMessageText;
    private MessageHandler messageHandler;
    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Berechtigungen überprüfen
        permissionHelper = new PermissionHelper(this);
        permissionHelper.checkAndRequestForegroundPermissions();

        // Initialisieren der Handler und Listener
        messageHandler = MessageHandler.getInstance(this);
        safeMode = safeMode.getInstance(this);
        locationListener = new LocationListener(this, this);
        buttonHandler = new ButtonHandler(findViewById(R.id.sosButton), findViewById(R.id.timerText), findViewById(R.id.progressBar), findViewById(R.id.exitSafeModeButton), safeMode, this);
        fireBaseHelper = new FireBaseHelper();

        // Info-Button setzen
        Button infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> InfoDialogHelper.showAppInfo(MainActivity.this));

        // Beobachtung des Sicherheitsmodus
        safeMode.getSafeModeLiveData().observe(this, isActive -> {
            if (isActive) {
                locationListener.getLocation();  // Standort-Updates starten
                Log.d("MainActivity", "SafeMode aktiv: Standort-Updates gestartet");
            } else {
                locationListener.stopLocationUpdates();  // Standort-Updates stoppen
                Log.d("MainActivity", "SafeMode inaktiv: Standort-Updates gestoppt");
            }
        });
    }

    // Startet die Einstellungen-Aktivität
    public void launchSettings(View v) {
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }

    // Bearbeitung der Berechtigungsanfragen
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Stoppt Standort-Updates beim Zerstören der Aktivität
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageHandler.getInstance(this).stopSendingLocationUpdates();
    }
}