package com.example.feelsave;

import android.app.Activity;
import android.content.Context;

public class ObjectManager {

    private static ObjectManager instance;

    private SafeMode safeMode;
    private MessageHandler messageHandler;
    private FireBaseHelper fireBaseHelper;

    private ObjectManager(){
        safeMode = new SafeMode();
        fireBaseHelper = new FireBaseHelper();
        //messageHandler = new MessageHandler(context);
    }

    public static synchronized ObjectManager getInstance(Context context) {
        if (instance == null) {
            instance = new ObjectManager();
        }
        return instance;
    }

    public MessageHandler getMessageHandlerInstance(){
        return messageHandler;
    }
    public SafeMode getSafeModeInstance(){
        return safeMode;
    }
    public FireBaseHelper getFireBaseHelperInstance(){
        return fireBaseHelper;
    }


}
