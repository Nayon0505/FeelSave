package com.example.feelsave;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ButtonHandler {
    private Button button;
    private Boolean sosButtonPressed = false;
    private TextView timerText;
    private CountDown countDown;
    private MessageHandler messageHandler;

    @SuppressLint("ClickableViewAccessibility")
    public ButtonHandler(Button button, TextView timerText, Button exitSafeModeButton, SafeMode safeMode, Context context) {
        this.countDown = new CountDown(context);
        this.button = button;
        this.timerText = timerText;



        this.button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sosButtonPressed = true;
                    if(!safeMode.getSafeModeStatus()) { //Wenn der SafeMode nicht an ist, wird er eingeleitet
                        countDown.startCountDown(5000,1000,exitSafeModeButton, timerText, safeMode);
                        Log.d("","Entering Safemode");
                         //davor wurde aus der CountDown klasse der SafeModeStatus geupdatet, was fehlerhaft war jetzt aus der SafeMode klasse
                        //Countdown wird gestartet und safeModeStatus auf true geupdatet onFinish
                    }
                    else if(safeMode.getSafeModeStatus()){
                        countDown.cancelCountDown(); //Wenn der safemode an ist und der Button wieder gedrückt wird, bricht der emergency countdown wieder ab
                    }

                    Log.d("Debug", "Button pressed " + sosButtonPressed);
                    Log.d("SafeMode Status", "safeMode = " + safeMode.getSafeModeStatus());

                    break;
                case MotionEvent.ACTION_UP:
                    sosButtonPressed = false;
                    if (!safeMode.getSafeModeStatus()) { //wenn der Countdown nicht zu ende geht und davor der Finger gehoben wird bricht der Countdown ab
                        countDown.cancelCountDown();

                    }
                    else if (safeMode.getSafeModeStatus()) { //Wenn der SafeMode an ist und der Button losgelassen wird, geht der emergency Countdown los.
                            countDown.startEmergencyCountdown(5000, 1000, timerText, safeMode, context);

                        }


                    v.performClick(); // Wichtig für Barrierefreiheit
                        Log.d("Debug", "Button pressed " + sosButtonPressed);
                        Log.d("SafeMode Status", "Safemode: "+ safeMode.getSafeModeStatus());


                    break;
            }
            return true;
        });

        exitSafeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safeMode.exitSafeMode();
                countDown.cancelCountDown();
                exitSafeModeButton.setVisibility(View.INVISIBLE);

            }
        });


    }


        }








