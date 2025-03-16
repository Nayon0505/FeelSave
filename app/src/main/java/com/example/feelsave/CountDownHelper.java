package com.example.feelsave;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Diese Klasse verwaltet alle Countdowns der App:
 * 1. Countdown für den Übergang in den SafeMode, wenn der SOS-Button gehalten wird.
 * 2. Notfall-Countdown, der nach dem Loslassen des Buttons startet.
 */
public class CountDownHelper {

    private CountDownTimer countDownTimer;
    private final MessageHandler messageHandler;

    public CountDownHelper(Context context) {
        messageHandler = MessageHandler.getInstance(context);
    }

    /**
     * Startet den Countdown für den Übergang in den SafeMode.
     * Wenn der Countdown abläuft, wird der SafeMode aktiviert.
     */
    public void startCountDown(
            int milliSecs,
            int countDownInterval,
            Button exitButton,
            TextView timerText,
            ProgressBar progressBar,
            SafeModeModel safeModeModel
    ) {
        countDownTimer = new CountDownTimer(milliSecs, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                // Aktualisiert den Timer-Text und Fortschrittsbalken
                timerText.setText("Safe-Modus in: " + (millisUntilFinished / 1000) + "s");
                timerText.setAlpha(0f);
                timerText.animate().alpha(1f).setDuration(300).start();
                progressBar.setProgress((int) (milliSecs - millisUntilFinished), true);
            }

            @Override
            public void onFinish() {
                // Aktiviert SafeMode, wenn er nicht bereits aktiv ist
                if (!safeModeModel.getSafeModeStatus()) {
                    handleSafeModeActivation(safeModeModel);
                    exitButton.setVisibility(View.VISIBLE);
                    timerText.setText("Sie befinden sich im Safemode.");
                    Log.d("CountDown", "SafeMode aktiviert: " + safeModeModel.getSafeModeStatus());
                }
            }
        }.start();
    }

    /**
     * Startet den Notfall-Countdown, wenn der SafeMode aktiv ist und der Button losgelassen wurde.
     * Nach Ablauf wird eine Notfallnachricht gesendet.
     */
    public void startEmergencyCountdown(
            int milliSecs,
            int countDownInterval,
            TextView timerText,
            ProgressBar progressBar,
            SafeModeModel safeModeModel,
            Context context
    ) {
        countDownTimer = new CountDownTimer(milliSecs, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Notfall in: " + millisUntilFinished / 1000);
                progressBar.setProgress((int) (milliSecs - millisUntilFinished), true);
            }

            @Override
            public void onFinish() {
                // Sendet Notfallnachricht, wenn SafeMode noch aktiv ist
                if (safeModeModel.getSafeModeStatus()) {
                    messageHandler.sendMessage();
                    messageHandler.startSendingLocationUpdates();
                    Log.d("Emergency Countdown", "Notfall ausgelöst: Standort-Updates und SMS gestartet");
                }
            }
        }.start();
    }

    /**
     * Aktiviert den SafeMode und speichert den Status.
     */
    public void handleSafeModeActivation(SafeModeModel safeModeModel) {
        safeModeModel.enterSafeMode();
        Log.d("SafeMode", "SafeMode aktiviert.");
    }

    /**
     * Stoppt den aktuellen Countdown und setzt die UI-Elemente zurück.
     */
    public void cancelCountDown(ProgressBar progressBar, TextView timerText) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            progressBar.setProgress(0);
            timerText.setText("");
            Log.d("Countdown", "Countdown abgebrochen.");
        }
    }
}
