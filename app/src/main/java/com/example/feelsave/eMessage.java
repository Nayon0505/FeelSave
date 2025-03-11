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

public class eMessage extends AppCompatActivity {

    private MessageHandler messageHandler;
    private String emergencyMessage;
    private EditText emergencyMessageInput;
    private FireBaseHelper fireBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_emergency_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        messageHandler = MessageHandler.getInstance(this);
        emergencyMessageInput = findViewById(R.id.emergencyMessageInputField);
        fireBaseHelper = new FireBaseHelper();

        emergencyMessageInput.setText(messageHandler.getMessageText());
        Log.d("eMessage", "Initial Message: " + messageHandler.getMessageText());

        fireBaseHelper.fetchEmergencyMessage().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String fetchedMessage = task.getResult();
                if (fetchedMessage != null) {
                    Log.d("Firebase", "Emergency Message: " + fetchedMessage);
                    // Aktualisiere MessageHandler und UI, falls eine Nachricht abgerufen wurde
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

    public void safeInput(View view) {
        emergencyMessage = emergencyMessageInput.getText().toString();
        messageHandler.setMessageText(emergencyMessage);
        Toast.makeText(this, "Nachricht gespeichert", Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed(View view) {
        finish();
    }
}
