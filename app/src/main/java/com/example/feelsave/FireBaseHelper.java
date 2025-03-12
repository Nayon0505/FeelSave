package com.example.feelsave;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//Regelt alle Datenbankoperationen
public class FireBaseHelper {
    private static final int MAX_LOCATIONS = 20;

    private String getUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("FireBaseHelper", "Kein Nutzer eingeloggt!");
            return null;
        }
        return user.getUid();
    }

    private DatabaseReference getUserReference(String subPath) {
        String uid = getUid();
        if (uid == null) return null;
        return FirebaseDatabase.getInstance().getReference("users").child(uid).child(subPath);
    }

    private String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }


    /**
     * Fügt einen Standort hinzu. Wird das Limit (MAX_LOCATIONS) erreicht, wird zuerst
     * der älteste Standort gelöscht, bevor der neue gespeichert wird.
     */
    public Task<Void> addLocation(String address) {
        DatabaseReference ref = getUserReference("Standorte");
        if (ref == null) return Tasks.forException(new Exception("User not logged in"));

        return ref.get().continueWithTask(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                return Tasks.forException(new Exception("Fehler beim Abrufen der Standorte"));
            }
            DataSnapshot snapshot = task.getResult();
            List<DataSnapshot> locationSnapshots = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                locationSnapshots.add(child);
            }
            // Wenn zu viele Standorte vorhanden sind, lösche den ältesten.
            if (locationSnapshots.size() >= MAX_LOCATIONS) {
                locationSnapshots.sort((a, b) -> a.child("Zeitpunkt").getValue(String.class)
                        .compareTo(b.child("Zeitpunkt").getValue(String.class)));
                String oldestKey = locationSnapshots.get(0).getKey();
                if (oldestKey != null) {
                    ref.child(oldestKey).removeValue()
                            .addOnSuccessListener(aVoid -> Log.d("FireBaseHelper", "Ältester Standort gelöscht"))
                            .addOnFailureListener(e -> Log.e("FireBaseHelper", "Fehler beim Löschen des ältesten Standorts", e));
                }
            }
            // Füge den neuen Standort hinzu.
            HashMap<String, Object> location = new HashMap<>();
            location.put("Adresse", address);
            location.put("Zeitpunkt", getCurrentTime());
            String key = ref.push().getKey();
            if (key != null) {
                location.put("key", key);
                return ref.child(key).setValue(location)
                        .addOnSuccessListener(aVoid -> Log.d("FireBaseHelper", "Standort gespeichert"))
                        .addOnFailureListener(e -> Log.e("FireBaseHelper", "Fehler beim Speichern des Standorts", e));
            } else {
                return Tasks.forException(new Exception("Fehler beim Generieren eines Schlüssels"));
            }
        });
    }

    /**
     * Fügt einen Kontakt hinzu und gibt den generierten Schlüssel zurück.
     */
    public Task<String> addContact(String name, String number) {
        DatabaseReference ref = getUserReference("Kontakte");
        if (ref == null) return Tasks.forException(new Exception("User not logged in"));
        HashMap<String, Object> contact = new HashMap<>();
        contact.put("Name", name);
        contact.put("Nummer", number);
        String key = ref.push().getKey();
        if (key != null) {
            contact.put("key", key);
            return ref.child(key).setValue(contact)
                    .addOnSuccessListener(aVoid -> Log.d("FireBaseHelper", "Kontakt gespeichert"))
                    .continueWith(task -> key);
        } else {
            return Tasks.forException(new Exception("Fehler beim Generieren eines Schlüssels"));
        }
    }

    /**
     * Löscht einen Kontakt anhand seines Schlüssels.
     */
    public Task<Void> deleteContact(String key) {
        DatabaseReference ref = getUserReference("Kontakte");
        if (ref == null || key == null || key.isEmpty())
            return Tasks.forException(new Exception("Ungültiger Schlüssel oder User nicht eingeloggt"));
        return ref.child(key).removeValue()
                .addOnSuccessListener(aVoid -> Log.d("FireBaseHelper", "Kontakt gelöscht"))
                .addOnFailureListener(e -> Log.e("FireBaseHelper", "Fehler beim Löschen des Kontakts", e));
    }

    /**
     * Speichert die Notfallnachricht in der Datenbank.
     */
    public Task<Void> addEmergencyMessage(String message) {
        DatabaseReference ref = getUserReference("Notfallnachricht");
        if (ref == null) return Tasks.forException(new Exception("User not logged in"));
        return ref.setValue(message)
                .addOnSuccessListener(aVoid -> Log.d("FireBaseHelper", "Notfallnachricht gespeichert"))
                .addOnFailureListener(e -> Log.e("FireBaseHelper", "Fehler beim Speichern der Nachricht", e));
    }

    /**
     * Ruft die Notfallnachricht aus der Datenbank ab.
     */
    public Task<String> fetchEmergencyMessage() {
        DatabaseReference ref = getUserReference("Notfallnachricht");
        if (ref == null) return Tasks.forException(new Exception("User not logged in"));
        return ref.get().continueWith(task -> {
            DataSnapshot snapshot = task.getResult();
            if (snapshot.exists()) {
                String message = snapshot.getValue(String.class);
                Log.d("FireBaseHelper", "Fetched emergency message: " + message);
                return message;
            } else {
                Log.d("FireBaseHelper", "Keine Notfallnachricht gefunden");
                return null;
            }
        });
    }

    /**
     * Lädt alle Notfallkontakte des Nutzers aus der Datenbank.
     */
    public Task<List<ContactModel>> fetchContacts() {
        DatabaseReference ref = getUserReference("Kontakte");
        if (ref == null) return Tasks.forException(new Exception("User not logged in"));
        return ref.get().continueWith(task -> {
            List<ContactModel> contacts = new ArrayList<>();
            DataSnapshot snapshot = task.getResult();
            for (DataSnapshot child : snapshot.getChildren()) {
                String name = child.child("Name").getValue(String.class);
                String number = child.child("Nummer").getValue(String.class);
                String key = child.getKey();
                if (name != null && number != null && key != null) {
                    contacts.add(new ContactModel(name, number, key));
                }
            }
            Log.d("FireBaseHelper", "Fetched " + contacts.size() + " contacts");
            return contacts;
        });
    }

    /**
     * Lädt alle Standorte des Nutzers aus der Datenbank.
     */
    public Task<List<LocationModel>> fetchLocations() {
        DatabaseReference ref = getUserReference("Standorte");
        if (ref == null) return Tasks.forException(new Exception("User not logged in"));
        return ref.get().continueWith(task -> {
            List<LocationModel> locations = new ArrayList<>();
            DataSnapshot snapshot = task.getResult();
            for (DataSnapshot child : snapshot.getChildren()) {
                String address = child.child("Adresse").getValue(String.class);
                String time = child.child("Zeitpunkt").getValue(String.class);
                String key = child.getKey();
                if (address != null && time != null && key != null) {
                    locations.add(new LocationModel(address, time, key));
                }
            }
            Log.d("FireBaseHelper", "Fetched " + locations.size() + " locations");
            return locations;
        });
    }
}