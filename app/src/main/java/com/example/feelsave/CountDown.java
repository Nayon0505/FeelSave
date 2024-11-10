package com.example.feelsave;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CountDown {

    private CountDownTimer countDownTimer;
    private MessageHandler messageHandler;


    public CountDown(Context context){
            messageHandler = MessageHandler.getInstance(context);
        }

    public void startCountDown(int milliSecs, int countDownInterval, Button exitButton, TextView timerText, SafeMode safeMode){ //Man braucht SafeMode hier wenn man den Kosntrukter freihalten will, sonst gettet er den Safemode eines falschen Objekts
        countDownTimer = new CountDownTimer(milliSecs, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {

                if(!safeMode.getSafeModeStatus()) {
                    handleSafeModeActivation(safeMode);
                    exitButton.setVisibility(View.VISIBLE);
                    Log.d("Class: CountDown","SafeMode entered. safemode:"+safeMode.getSafeModeStatus());

                }
                Log.d("","onFinish reached");
            }

        }.start();


    }

    public void startEmergencyCountdown(int milliSecs, int countDownInterval, TextView timerText, SafeMode safeMode, Context context) {
        countDownTimer = new CountDownTimer(milliSecs, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Emergency in: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {

                // Notfallnachricht senden, wenn der SafeMode noch aktiv ist
                if (safeMode.getSafeModeStatus()) {
                    messageHandler.sendMessage();
                    Log.d("Emergency", "Emergency message sent.");
                }
            }
        }.start();
    }

        public void handleSafeModeActivation(SafeMode safeMode){
            safeMode.enterSafeMode();
            Log.d("SafeMode Status", " handleSafeModeActication called... ");
        }

    public void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            Log.d("Countdown", "Countdown canceled.");
            }
        }

    }

