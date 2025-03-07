package com.example.feelsave;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactPicker {

    public static final int PICK_CONTACT_REQUEST = 1;

    // Startet den Kontakt-Picker
    public static void pickContact(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        activity.startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    // Liest die Kontaktdaten aus dem Result-Intent und gibt ein contactModel-Objekt zurück
    public static contactModel getContactData(Intent data, Activity activity) {
        Uri contactUri = data.getData();
        String name = "";
        String number = "";
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor cursor = activity.getContentResolver().query(contactUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                name = cursor.getString(nameIndex);
                number = cursor.getString(numberIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ein contactModel-Objekt mit dem Namen und der Nummer zurückgeben
        return new contactModel(name, number, null);
    }
}
