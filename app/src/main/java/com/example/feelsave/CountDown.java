package com.example.feelsave;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class CountDown {

    private CountDownTimer countDownTimer;
    private MessageHandler messageHandler;


    public CountDown(Context context){
            messageHandler = MessageHandler.getInstance(context);
        }

    public void startCountDown(int milliSecs, int countDownInterval, Button exitButton, TextView timerText, ProgressBar progressBar, SafeMode safeMode){ //Man braucht SafeMode hier wenn man den Kosntrukter freihalten will, sonst gettet er den Safemode eines falschen Objekts
        countDownTimer = new CountDownTimer(milliSecs, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                String timeText = "Safe-Modus in: " + (millisUntilFinished / 1000) + "s";
                timerText.setText(timeText);
                timerText.setAlpha(0f);
                timerText.animate().alpha(1f).setDuration(300).start();
                int progress = (int)(milliSecs - millisUntilFinished);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {

                if(!safeMode.getSafeModeStatus()) {
                    handleSafeModeActivation(safeMode);
                    exitButton.setVisibility(View.VISIBLE);
                    timerText.setText("Sie befinden sich im Safemode.");
                    Log.d("Class: CountDown","SafeMode entered. safemode:"+safeMode.getSafeModeStatus());

                }
                Log.d("","onFinish reached");
            }

        }.start();


    }

    public void startEmergencyCountdown(int milliSecs, int countDownInterval, TextView timerText,ProgressBar progressBar, SafeMode safeMode, Context context) {
        countDownTimer = new CountDownTimer(milliSecs, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Notfall in: " + millisUntilFinished / 1000);
                int progress = (int)(milliSecs - millisUntilFinished);
                progressBar.setProgress(progress);
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

    public void cancelCountDown(ProgressBar progressBar, TextView timerText) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            progressBar.setProgress(0);
            timerText.setText("");

            Log.d("Countdown", "Countdown canceled.");
            }
        }

    }

