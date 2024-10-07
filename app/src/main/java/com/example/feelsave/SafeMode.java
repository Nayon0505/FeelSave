package com.example.feelsave;

import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SafeMode {

    private boolean safeModeStatus;

    private Button exitSafeModeButton;

    public SafeMode(Button exitSafeModeButton){
        this.exitSafeModeButton = exitSafeModeButton;
    }
    public boolean enterSafeMode(){
        this.safeModeStatus = true;
        exitSafeModeButton.setVisibility(View.VISIBLE);
        Log.d("Class: SafeMode", "SafeMode: " + safeModeStatus);
        return safeModeStatus;
    }
    public boolean exitSafeMode(){
        this.safeModeStatus = false;
        exitSafeModeButton.setVisibility(View.INVISIBLE);
        Log.d("Class: SafeMode", "SafeMode: " + safeModeStatus);
        return safeModeStatus;
    }

    public boolean updateSafeModeStatus() {
        return safeModeStatus;
    }



}
