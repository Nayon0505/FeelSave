package com.example.feelsave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;


//Die Einstellungsactivity dient hauptsächlich als Navigator

public class Settings extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        firebaseAuth.signOut();
        Toast.makeText(this, "Abgemeldet", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Settings.this, UserAuth.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Beendet alle vorherigen Aktivitäten
        startActivity(intent);
        finish(); // Beendet die aktuelle Activity
    }


    public void launchEmergencyMessageActivity(View v){
        Intent i = new Intent(this, EmergencyMessage.class);
        startActivity(i);
    }
    public void launchEmergencyContactsActivity(View v){
        Intent i = new Intent(this, EmergencyContacts.class);
        startActivity(i);
    }

    public void onBackPressed(View view) {
        finish();
    }


}