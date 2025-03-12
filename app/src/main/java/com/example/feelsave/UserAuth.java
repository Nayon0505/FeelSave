package com.example.feelsave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Login und Register Activity, hier werden Nutzer angemeldet, registriert oder abgemeldet

public class UserAuth extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button registerButton, loginButton, logoutButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        // Initialisierung von Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Zuweisung der UI-Komponenten
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Setzen der Click-Listener für die Buttons
        registerButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> loginUser());
        logoutButton.setOnClickListener(v -> logoutUser());

        // Überprüfen, ob der Benutzer bereits eingeloggt ist
        checkUserStatus();
    }

    // Registrierung eines neuen Benutzers
    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validierung der Eingaben
        if (email.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "E-Mail und Passwort (min. 6 Zeichen) erforderlich", Toast.LENGTH_SHORT).show();
            return;
        }

        // Benutzer mit E-Mail und Passwort registrieren
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registrierung erfolgreich", Toast.LENGTH_SHORT).show();
                        checkUserStatus(); // Benutzerstatus nach erfolgreicher Registrierung überprüfen
                    } else {
                        Toast.makeText(this, "Fehler: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Anmeldung eines bestehenden Benutzers
    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validierung der Eingaben
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "E-Mail und Passwort erforderlich", Toast.LENGTH_SHORT).show();
            return;
        }

        // Anmeldung des Benutzers mit E-Mail und Passwort
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login erfolgreich", Toast.LENGTH_SHORT).show();
                        checkUserStatus(); // Benutzerstatus nach erfolgreichem Login überprüfen
                    } else {
                        Toast.makeText(this, "Nutzer existiert nicht", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Abmeldung des aktuell angemeldeten Benutzers
    public void logoutUser() {
        firebaseAuth.signOut(); // Abmelden des Benutzers
        Toast.makeText(this, "Abgemeldet", Toast.LENGTH_SHORT).show();
        checkUserStatus(); // Benutzerstatus nach der Abmeldung überprüfen
    }

    // Überprüfung des aktuellen Benutzerstatus
    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Falls der Benutzer eingeloggt ist, Weiterleitung zur MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Aktuelle Aktivität schließen
        }
    }
}
