package com.example.feelsave;

import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //private boolean safeMode;
    private SosButton sosButtonHandler;
    private SafeMode safeMode;
    private CountDown countDown;
    private LocationListener locationListener;



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

        locationListener = new LocationListener(this,this);
        locationListener.checkLocationPermisson(this);
        sosButtonHandler = new SosButton(findViewById(R.id.sosButton), findViewById(R.id.timerText),findViewById(R.id.exitSafeModeButton));


    }

    public void test(View v){
        locationListener.getLocation();


    }
}