package com.example.feelsave;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

//Der Safemode stellt den Sicherheitsstatus des Nutzers dar. Hier wird sich um diesen gekümmert
public class SafeModeModel {
    private static SafeModeModel instance;



    private final MutableLiveData<Boolean> safeModeStatus = new MutableLiveData<>(false);

    public SafeModeModel(Context context){

    }
    public boolean enterSafeMode(){
        safeModeStatus.setValue(true);
        Log.d("Class: SafeMode", "SafeMode: " + safeModeStatus);
        return true;
    }
    public boolean exitSafeMode(){
        safeModeStatus.setValue(false);
        Log.d("Class: SafeMode", "SafeMode: " + safeModeStatus);
        return false;
    }

    public boolean getSafeModeStatus() {
        Boolean status = safeModeStatus.getValue();
        return status != null && status;
    }

    public LiveData<Boolean> getSafeModeLiveData() {
        return safeModeStatus;
    }
    public static synchronized SafeModeModel getInstance(Context context) {
        if (instance == null) {
            instance = new SafeModeModel(context);
        }
        return instance;
    }


}
