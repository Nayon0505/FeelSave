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

public class UserAuth extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button registerButton, loginButton, logoutButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        // Firebase Auth initialisieren
        firebaseAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);

        registerButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> loginUser());
        logoutButton.setOnClickListener(v -> logoutUser());

        // Falls User eingeloggt ist, zeige Logout-Button
        checkUserStatus();
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "E-Mail und Passwort (min. 6 Zeichen) erforderlich", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registrierung erfolgreich", Toast.LENGTH_SHORT).show();
                        checkUserStatus();
                    } else {
                        Toast.makeText(this, "Fehler: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "E-Mail und Passwort erforderlich", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login erfolgreich", Toast.LENGTH_SHORT).show();
                        checkUserStatus();
                    } else {
                        Toast.makeText(this, "Fehler: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutUser() {
        firebaseAuth.signOut();
        Toast.makeText(this, "Abgemeldet", Toast.LENGTH_SHORT).show();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Falls eingeloggt, direkt zur MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

}

}
