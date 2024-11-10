package com.example.feelsave;

import android.util.Log;

public class SafeMode {

    private boolean safeModeStatus;


    public SafeMode(){

    }
    public boolean enterSafeMode(){
        this.safeModeStatus = true;
        Log.d("Class: SafeMode", "SafeMode: " + safeModeStatus);
        return safeModeStatus;
    }
    public boolean exitSafeMode(){
        this.safeModeStatus = false;
        Log.d("Class: SafeMode", "SafeMode: " + safeModeStatus);
        return safeModeStatus;
    }

    public boolean getSafeModeStatus() {
        return safeModeStatus;
    }



}
