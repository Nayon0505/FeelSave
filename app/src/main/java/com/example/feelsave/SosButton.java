package com.example.feelsave;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SosButton {
    private Button button;
    private Emergency emergency = new Emergency();
    private Boolean sosButtonPressed = false;
    private TextView timerText;
    private CountDown countDown;
    private boolean safeModeStatus;
    private Button exitSafeModeButton;
    private SafeMode safeMode;

    @SuppressLint("ClickableViewAccessibility")
    public SosButton(Button button, TextView timerText,Button exitSafeModeButton) {

        this.safeMode= new SafeMode(exitSafeModeButton);
        this.countDown = new CountDown(timerText);
        this.button = button;
        this.timerText = timerText;
        this.exitSafeModeButton = exitSafeModeButton;

        this.button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sosButtonPressed = true;
                    if(!safeModeStatus) { //Wenn der SafeMode nicht an ist, wird er eingeleitet
                        countDown.startCountDown(5000,1000,safeMode,exitSafeModeButton);
                        Log.d("","Entering Safemode");
                        safeModeStatus=safeMode.updateSafeModeStatus(); //davor wurde aus der CountDown klasse der SafeModeStatus geupdatet, was fehlerhaft war jetzt aus der SafeMode klasse
                        //Countdown wird gestartet und safeModeStatus auf true geupdatet onFinish
                    }
                    else if(safeModeStatus){
                        countDown.cancelCountDown(); //Wenn der safemode an ist und der Button wieder gedrückt wird, bricht der emergency countdown wieder ab
                    }
                    safeModeStatus=safeMode.updateSafeModeStatus();
                    Log.d("Debug", "Button pressed " + sosButtonPressed);
                    Log.d("SafeMode Status", "safeMode = " + safeModeStatus);

                    break;
                case MotionEvent.ACTION_UP:
                    safeModeStatus = safeMode.updateSafeModeStatus();
                    sosButtonPressed = false;
                    if (!safeModeStatus) { //wenn der Countdown nicht zu ende geht und davor der Finger gehoben wird bricht der Countdown ab
                        countDown.cancelCountDown();

                    }
                    else if (safeModeStatus) { //Wenn der SafeMode an ist und der Button losgelassen wird, geht der emergency Countdown los.
                            countDown.startEmergencyCountdown(5000, 1000, emergency, safeMode);

                        }


                    v.performClick(); // Wichtig für Barrierefreiheit
                        Log.d("Debug", "Button pressed " + sosButtonPressed);
                        Log.d("SafeMode Status", "Safemode: "+ safeModeStatus);


                    break;
            }
            return true;
        });

        exitSafeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               safeModeStatus = safeMode.exitSafeMode();
               countDown.cancelCountDown();
               Log.d("","Safemode: "+ safeModeStatus);

            }
        });
    }


        }








