package com.example.feelsave;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Der ButtonHandler steuert die Interaktionen mit dem SOS-Button
 * und verwaltet den SafeMode sowie die zugehörigen Countdowns.
 */
public class ButtonHandler {
    private final Button button;
    private Boolean sosButtonPressed = false;
    private final TextView timerText;
    private final CountDown countDown;
    private MessageHandler messageHandler;

    /**
     * Konstruktor für den ButtonHandler. Hier werden UI-Elemente und Logiken verknüpft.
     */
    @SuppressLint("ClickableViewAccessibility")
    public ButtonHandler(
            Button sosButton,
            TextView timerText,
            ProgressBar progressBar,
            Button exitSafeModeButton,
            SafeMode safeMode,
            Context context
    ) {
        this.countDown = new CountDown(context);
        this.button = sosButton;
        this.timerText = timerText;
        Animation pulse = AnimationUtils.loadAnimation(context, R.anim.pulse);

        // Logik für das Drücken des SOS-Buttons
        this.button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sosButton.startAnimation(pulse);
                    sosButtonPressed = true;

                    // Startet Countdown, wenn SafeMode nicht aktiv ist
                    if (!safeMode.getSafeModeStatus()) {
                        countDown.startCountDown(5000, 1000, exitSafeModeButton, timerText, progressBar, safeMode);
                        Log.d("", "Entering Safemode");
                    }
                    // Stoppt den Countdown, wenn SafeMode aktiv ist
                    else {
                        countDown.cancelCountDown(progressBar, timerText);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    sosButton.clearAnimation();
                    sosButtonPressed = false;

                    // Bricht Countdown ab oder startet den Notfall-Countdown
                    if (!safeMode.getSafeModeStatus()) {
                        countDown.cancelCountDown(progressBar, timerText);
                    } else {
                        countDown.startEmergencyCountdown(5000, 1000, timerText, progressBar, safeMode, context);
                    }
                    break;
            }
            return true;
        });

        // Logik für den Exit-SafeMode-Button
        exitSafeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageHandler = MessageHandler.getInstance(context);
                messageHandler.stopSendingLocationUpdates();
                safeMode.exitSafeMode();
                countDown.cancelCountDown(progressBar, timerText);
                exitSafeModeButton.setVisibility(View.INVISIBLE);
            }
        });
    }
}
