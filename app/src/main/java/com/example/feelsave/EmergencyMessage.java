package com.example.feelsave;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Diese Activity ermöglicht dem Nutzer das Bearbeiten seiner Notfallnachricht.
 * Die Nachricht wird in Firebase gespeichert und über den MessageHandler verwaltet.
 */
public class EmergencyMessage extends AppCompatActivity {

    private MessageHandler messageHandler;
    private String emergencyMessage;
    private EditText emergencyMessageInput;
    private FireBaseHelper fireBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_emergency_message);

        // Passt das Layout an die Systemleisten (z. B. Statusleiste) an
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisierung der Klassen für Nachrichtenverwaltung und Firebase-Zugriff
        messageHandler = MessageHandler.getInstance(this);
        fireBaseHelper = new FireBaseHelper();

        // Verknüpft das Eingabefeld mit dem UI-Element
        emergencyMessageInput = findViewById(R.id.emergencyMessageInputField);

        // Setzt den gespeicherten Nachrichtentext als Standardwert im Eingabefeld
        emergencyMessageInput.setText(messageHandler.getMessageText());
        Log.d("eMessage", "Initial Message: " + messageHandler.getMessageText());

        // Lädt die Notfallnachricht aus Firebase und aktualisiert das Eingabefeld
        fireBaseHelper.fetchEmergencyMessage().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String fetchedMessage = task.getResult();
                if (fetchedMessage != null) {
                    Log.d("Firebase", "Emergency Message: " + fetchedMessage);
                    // Nachricht in MessageHandler aktualisieren und im UI anzeigen
                    messageHandler.setMessageText(fetchedMessage);
                    emergencyMessageInput.setText(fetchedMessage);
                } else {
                    Log.d("Firebase", "Keine Emergency Message vorhanden.");
                }
            } else {
                Log.e("Firebase", "Fehler beim Abrufen der Emergency Message", task.getException());
            }
        });
    }

    /**
     * Speichert die eingegebene Notfallnachricht lokal über den MessageHandler.
     */
    public void safeInput(View view) {
        emergencyMessage = emergencyMessageInput.getText().toString();
        messageHandler.setMessageText(emergencyMessage);
        Toast.makeText(this, "Nachricht gespeichert", Toast.LENGTH_SHORT).show();
    }

    /**
     * Beendet die Activity und kehrt zur vorherigen Ansicht zurück.
     */
    public void onBackPressed(View view) {
        finish();
    }
}
