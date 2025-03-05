package com.example.feelsave;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    private ButtonHandler buttonHandler;
    private SafeMode safeMode;
    private CountDown countDown;
    private LocationListener locationListener;
    private PermissionHandler permissionHandler;
    private FireBaseHelper fireBaseHelper;
    private ObjectManager objectManager;
    private TextView emergencyMessageText;
    private MessageHandler messageHandler;




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

        messageHandler = MessageHandler.getInstance(this);
        safeMode = ObjectManager.getInstance(this).getSafeModeInstance();
        locationListener = new LocationListener(this,this);
        permissionHandler = new PermissionHandler(this, this);
        buttonHandler = new ButtonHandler(findViewById(R.id.sosButton), findViewById(R.id.timerText),findViewById(R.id.progressBar),findViewById(R.id.exitSafeModeButton), safeMode, this);
        fireBaseHelper = new FireBaseHelper();
        Button infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> {
            InfoDialogHelper.showAppInfo(MainActivity.this);
        });

        //fireBaseHelper.readEmergencyMessageFromDB(emergencyMessageText);
        //emergency = new Emergency(this);


    }

    public void test(View v){
        if(safeMode.getSafeModeStatus()) {
            locationListener.getLocation();

        }
    }

    public void launchSettings(View v){
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }


}