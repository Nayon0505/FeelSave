package com.example.feelsave;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler {

    private static MessageHandler instance;

    private SmsManager smsManager;
    private ArrayList<String> phoneNumberList;
    private final String scAddress = null;
    private String messageText;
    private PendingIntent sentIntent;
    private PendingIntent deliveryIntent;
    private Context context;
    private FireBaseHelper fireBaseHelper;
    private List<contactModel> contactList;
    private List<LocationModel> locationList;
    private static final String DEFAULT_EMERGENCY_MESSAGE = "Ich benötige dringend Hilfe! Bitte komm sofort!";


    private MessageHandler(Context context) {
        this.context = context.getApplicationContext();
        this.phoneNumberList = new ArrayList<>();
        this.smsManager = SmsManager.getDefault();

        Intent sentIntentAction = new Intent("SMS_SENT");
        this.sentIntent = PendingIntent.getBroadcast(context, 0, sentIntentAction, PendingIntent.FLAG_IMMUTABLE);
        Intent deliveryIntentAction = new Intent("SMS_DELIVERED");
        this.deliveryIntent = PendingIntent.getBroadcast(context, 0, deliveryIntentAction, PendingIntent.FLAG_IMMUTABLE);

        this.fireBaseHelper = new FireBaseHelper();

        contactList = new ArrayList<>();
        locationList = new ArrayList<>();

        // Notfallnachricht abrufen
        fireBaseHelper.fetchEmergencyMessage().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String fetchedMessage = task.getResult();
                if (fetchedMessage == null || fetchedMessage.isEmpty()) {
                    // Kein Eintrag vorhanden: Standardnachricht setzen
                    setMessageText(DEFAULT_EMERGENCY_MESSAGE);
                    messageText = DEFAULT_EMERGENCY_MESSAGE;
                    Log.i("MessageHandler", "Standardnotfallnachricht gesetzt");
                } else {
                    messageText = fetchedMessage;
                    Log.i("MessageHandler", "Notfallnachricht abgerufen: " + messageText);
                }
            } else {
                Log.e("MessageHandler", "Error fetching emergency message", task.getException());
                messageText = DEFAULT_EMERGENCY_MESSAGE;
            }
        });

        // Kontakte abrufen
        fireBaseHelper.fetchContacts().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<contactModel> contacts = task.getResult();
                contactList.clear();
                if (contacts != null) {
                    contactList.addAll(contacts);
                }
                Log.i("MessageHandler", "Fetched contacts: " + contactList.size());
            } else {
                Log.e("MessageHandler", "Error fetching contacts", task.getException());
            }
        });

        // Standorte abrufen
        fireBaseHelper.fetchLocations().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<LocationModel> locations = task.getResult();
                locationList.clear();
                if (locations != null) {
                    locationList.addAll(locations);
                }
                Log.i("MessageHandler", "Fetched locations: " + locationList.size());
            } else {
                Log.e("MessageHandler", "Error fetching locations", task.getException());
            }
        });
    }

    public static synchronized MessageHandler getInstance(Context context) {
        if (instance == null) {
            instance = new MessageHandler(context);
        }
        return instance;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
        // Speichere die Notfallnachricht in Firebase
        fireBaseHelper.addEmergencyMessage(messageText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MessageHandler", "New emergency message saved: " + messageText);
                    } else {
                        Log.e("MessageHandler", "Failed to save emergency message", task.getException());
                    }
                });
    }

    public void addEmergencyContact(String name, String phoneNumber) {
        phoneNumberList.add(phoneNumber);
    }

    public void removeEmergencyContact(int i) {
        phoneNumberList.remove(i);
    }

    public void sendMessage() {
        if (messageText == null || messageText.isEmpty()) {
            Log.e("MessageHandler", "Invalid message body for sending SMS.");
            Toast.makeText(context, "Invalid message body!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (locationList.isEmpty()) {
            Toast.makeText(context, "Kein Standort verfügbar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verwende den letzten Standort in der Liste
        LocationModel lastLocation = locationList.get(locationList.size() - 1);
        String fullMessage = messageText + " Letzter Standort: "
                + lastLocation.getAdress() + " Uhrzeit: " + lastLocation.getTime();
        for (contactModel contact : contactList) {
            try {
                smsManager.sendTextMessage(contact.getNumber(), scAddress, fullMessage, sentIntent, deliveryIntent);
                Toast.makeText(context, "Nachricht gesendet!", Toast.LENGTH_SHORT).show();
                Log.i("MessageHandler", "Message sent to: " + contact.getNumber() + " - " + fullMessage);
            } catch (Exception e) {
                Log.e("MessageHandler", "Error sending SMS: " + e.getMessage());
                Toast.makeText(context, "Failed to send message!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final Handler handler = new Handler();
    private Runnable sendLocationRunnable;

    public void startSendingLocationUpdates() {
        if (sendLocationRunnable == null) {
            sendLocationRunnable = new Runnable() {
                @Override
                public void run() {
                    sendMessage();
                    handler.postDelayed(this, 10000); // Alle 10 Sekunden erneut ausführen
                }
            };
            handler.post(sendLocationRunnable);
        }
    }

    public void stopSendingLocationUpdates() {
        if (sendLocationRunnable != null) {
            handler.removeCallbacks(sendLocationRunnable);
            sendLocationRunnable = null;
        }
    }

}
