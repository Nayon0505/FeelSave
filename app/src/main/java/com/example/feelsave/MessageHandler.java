package com.example.feelsave;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler {

    private static MessageHandler instance;

    private SmsManager smsManager;
    private ArrayList<String> phoneNumberList;
    private String scAddress = null;
    private String messageText;
    private PendingIntent sentIntent;
    private PendingIntent deliveryIntent;
    private Context context;
    private FireBaseHelper fireBaseHelper;
    private List<contactModel> contactList;
    private List<LocationModel> locationList;



    private MessageHandler(Context context){
        this.context = context.getApplicationContext(); // Use application context to prevent memory leaks
        this.phoneNumberList = new ArrayList<>();
        this.smsManager = SmsManager.getDefault();
        Intent sentIntentAction = new Intent("SMS_SENT");
        this.sentIntent = PendingIntent.getBroadcast(context, 0, sentIntentAction, PendingIntent.FLAG_IMMUTABLE);
        Intent deliveryIntentAction = new Intent("SMS_DELIVERED");
        this.deliveryIntent = PendingIntent.getBroadcast(context, 0, deliveryIntentAction, PendingIntent.FLAG_IMMUTABLE);
        this.fireBaseHelper = ObjectManager.getInstance(context).getFireBaseHelperInstance();
        contactList = new ArrayList<>();
        locationList = new ArrayList<>();

        fireBaseHelper.fetchEmergencyMessage(new FireBaseHelper.FirebaseCallback() {
            @Override
            public void onContactsFetched(List<contactModel> contacts) {

            }

            @Override
            public void onEmergencyMessageFetched(String emergencyMessage) {
                messageText = emergencyMessage;
                Log.i("MessageHandler", "Message: "+ messageText);
            }

            @Override
            public void onLocationFetched(List<LocationModel> location) {

            }

        });
        fireBaseHelper.fetchContacts(new FireBaseHelper.FirebaseCallback() {
            @Override
            public void onContactsFetched(List<contactModel> contacts) {
                contactList.clear();  // Clear the old list
                contactList.addAll(contacts);  // Add the updated list
            }

            @Override
            public void onEmergencyMessageFetched(String emergencyMessage) {

            }

            @Override
            public void onLocationFetched(List<LocationModel> location) {

            }


        });

        fireBaseHelper.fetchLocation(new FireBaseHelper.FirebaseCallback() {
            @Override
            public void onContactsFetched(List<contactModel> contacts) {

            }

            @Override
            public void onEmergencyMessageFetched(String emergencyMessage) {

            }

            @Override
            public void onLocationFetched(List<LocationModel> location) {
                locationList.clear();
                locationList.addAll(location);
            }

        });
    }




    // Static method to get the instance
    public static synchronized MessageHandler getInstance(Context context) {
        if (instance == null) {
            instance = new MessageHandler(context);
        }
        return instance;
    }
    public String getMessageText(){
        return messageText;
    }

public void setMessageText(String messageText){
        this.messageText = messageText;
        fireBaseHelper.addEmergencyMessageToDB(messageText);
        Log.d("MessageHandler", "New EmergencyMessage: "+messageText);
}


public void addEmergencyContact(String name,String phoneNumber){
        phoneNumberList.add(phoneNumber);
}
public void removeEmergencyContact(int i){
        phoneNumberList.remove(i);
}
public void sendMessage(){

    if (messageText == null || messageText.isEmpty()) {
        Log.e("MessageHandler", "Invalid message body for sending SMS.");
        Toast.makeText(context, "Invalid message body!", Toast.LENGTH_SHORT).show();
        return;
    }

    try {
        messageText += " Letzter Standort: " + locationList.get(locationList.size()-1).getAdress()+ " Uhrzeit " + locationList.get(locationList.size()-1).getTime();
        for(int i = 0; i<contactList.size();i++) {
            smsManager.sendTextMessage(contactList.get(i).getNumber(), scAddress, messageText, sentIntent, deliveryIntent);
            Toast.makeText(context, "Nachricht gesendet!", Toast.LENGTH_SHORT).show();
            Log.i("MessageHandler", messageText + " sent to: " + contactList.get(i).getNumber() +" "+ scAddress+" " + sentIntent +" "+ deliveryIntent);
        }
    } catch (Exception e) {
        Log.e("MessageHandler", "Error sending SMS: " + e.getMessage());
        Toast.makeText(context, "Failed to send message!", Toast.LENGTH_SHORT).show();
    }
}



}
