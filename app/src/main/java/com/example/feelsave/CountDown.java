package com.example.feelsave;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class CountDown {

    private CountDownTimer countDownTimer;
    private TextView timerText;
    private boolean safeModeStatus = false;


    public CountDown(TextView timerText){
        this.timerText = timerText;

        }

    public void startCountDown(int milliSecs, int countDownInterval, SafeMode safeMode, Button exitButton){
        countDownTimer = new CountDownTimer(milliSecs, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                safeModeStatus= safeMode.updateSafeModeStatus();
                if(!safeModeStatus) {
                    handleSafeModeActivation(safeMode,exitButton);
                    Log.d("Class: CountDown","SafeMode entered. safemode:"+safeModeStatus);

                }
                Log.d("","onFinish reached");
            }

        }.start();


    }

    public void startEmergencyCountdown(int milliSecs, int countDownInterval, Emergency emergency, SafeMode safeMode) {
        countDownTimer = new CountDownTimer(milliSecs, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Emergency in: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                safeModeStatus= safeMode.updateSafeModeStatus();
                // Notfallnachricht senden, wenn der SafeMode noch aktiv ist
                if (safeModeStatus) {
                    emergency.sendEmergencyMessage();
                    Log.d("Emergency", "Emergency message sent.");
                }
            }
        }.start();
    }

        public boolean updateSafeModeStatus(){
        return safeModeStatus;
        }

        public void handleSafeModeActivation(SafeMode safeMode, Button exitButton){
            timerText.setText("Safemode!");
            safeModeStatus=safeMode.enterSafeMode();
            Log.d("SafeMode Status", " handleSafeModeActication called... ");
        }

    public void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            Log.d("Countdown", "Countdown canceled.");
            }
        }

    }

