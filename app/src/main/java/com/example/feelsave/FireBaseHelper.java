package com.example.feelsave;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FireBaseHelper {

    DatabaseReference databaseReference;

    public FireBaseHelper() {
        // Constructor can be used to initialize any necessary components if needed
    }

    public String getTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        Log.d("Current Time", "Time: " + formattedTime);
        return formattedTime;
    }

    public void initializeDbConection(String speicherPath) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.databaseReference = database.getReference(speicherPath);
    }

    public void addLocationToDB(String address) {
        HashMap<String, Object> locationHashmap = new HashMap<>();
        locationHashmap.put("Adresse", address);
        locationHashmap.put("Zeitpunkt", getTime());

        initializeDbConection("Standorte");

        String key = databaseReference.push().getKey();
        locationHashmap.put("key", key);

        if (key != null) {
            databaseReference.child(key).setValue(locationHashmap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Location added successfully");
                } else {
                    Log.e("Firebase", "Failed to add location", task.getException());
                }
            });
        } else {
            Log.e("Firebase", "Failed to generate unique key");
        }
    }

    public String addContactsToDB(String name, String number) {
        HashMap<String, Object> contactsHashmap = new HashMap<>();
        contactsHashmap.put("Name: ", name);
        contactsHashmap.put("Nummer: ", number);

        initializeDbConection("Kontakte");

        String key = databaseReference.push().getKey();
        contactsHashmap.put("key", key);

        if (key != null) {
            databaseReference.child(key).setValue(contactsHashmap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Contact added successfully");
                } else {
                    Log.e("Firebase", "Failed to add contact", task.getException());
                }
            });
        } else {
            Log.e("Firebase", "Failed to generate unique key");
        }
        return key;
    }

    public void deleteContactFromDB(String key) {
        if (key != null && !key.isEmpty()) {
            initializeDbConection("Kontakte");
            databaseReference.child(key).removeValue()
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Contact deleted successfully"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete contact", e));
        } else {
            Log.e("Firebase", "Invalid key for deletion");
        }
    }

    public void fetchContacts(FirebaseCallback callback) {
        initializeDbConection("Kontakte");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<contactModel> contacts = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String name = data.child("Name: ").getValue(String.class);
                    String number = data.child("Nummer: ").getValue(String.class);
                    String key = data.getKey();  // Unique key for each contact

                    if (name != null && number != null) {
                        contacts.add(new contactModel(name, number, key));
                    }
                }
                Log.d("FirebaseHelper", "Fetched " + contacts.size() + " contacts from Firebase");
                callback.onContactsFetched(contacts);  // Pass list to the callback
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Failed to read contacts", error.toException());
            }
        });
    }

    public void addEmergencyMessageToDB(String message) {
        initializeDbConection("Notfallnachricht");
        databaseReference.setValue(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Message added successfully");
            } else {
                Log.e("Firebase", "Failed to add Message", task.getException());
            }
        });
    }

    public void fetchEmergencyMessage(FirebaseCallback callback) {
        initializeDbConection("Notfallnachricht");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String emergencyMessage = snapshot.getValue(String.class);
                if (emergencyMessage != null) {
                    Log.d("FirebaseHelper", "Fetched emergency message: " + emergencyMessage);
                    callback.onEmergencyMessageFetched(emergencyMessage);  // Pass the message to the callback
                } else {
                    Log.d("FirebaseHelper", "No emergency message found.");
                    callback.onEmergencyMessageFetched(null); // Pass null if no message is found
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Failed to read emergency message", error.toException());
                callback.onEmergencyMessageFetched(null); // Pass null on error
            }
        });
    }

    public void fetchLocation(FirebaseCallback callback) {
        initializeDbConection("Standorte");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<LocationModel> locations = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String adress = data.child("Adresse").getValue(String.class);
                    String time = data.child("Zeitpunkt").getValue(String.class);
                    String key = data.getKey();  // Unique key for each contact

                    if (adress != null && time != null) {
                        locations.add(new LocationModel(adress, time, key));
                    }
                }
                Log.d("FirebaseHelper", "Fetched " + locations.size() + " contacts from Firebase");
                callback.onLocationFetched(locations);  // Pass list to the callback
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Failed to read contacts", error.toException());
            }
        });
    }

    // New callback interface to differentiate between contacts and emergency messages
    public interface FirebaseCallback {
        void onContactsFetched(List<contactModel> contacts); // For contacts
        void onEmergencyMessageFetched(String emergencyMessage); // For emergency messages
        void onLocationFetched(List<LocationModel> location);
    }
}
