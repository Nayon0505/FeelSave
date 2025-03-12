package com.example.feelsave;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity zur Verwaltung von Notfallkontakten.
 * Der Nutzer kann hier Kontakte hinzufügen, bearbeiten und löschen.
 */
public class EmergencyContacts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<ContactModel> contactList;
    private EditText nameInput;
    private EditText numberInput;
    private Button addButton;
    private Button selectContactButton;
    private FireBaseHelper fireBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacts);

        // Stellt sicher, dass das UI-Layout die Bildschirmränder berücksichtigt
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisierung der UI-Elemente und Firebase-Hilfsklasse
        fireBaseHelper = new FireBaseHelper();
        recyclerView = findViewById(R.id.recycler);
        nameInput = findViewById(R.id.nameInput);
        numberInput = findViewById(R.id.numberInput);
        addButton = findViewById(R.id.addButton);
        selectContactButton = findViewById(R.id.selectContactButton);

        // Setzt das RecyclerView für die Kontaktliste auf
        contactList = new ArrayList<>();
        adapter = new RecyclerAdapter(contactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Öffnet den Kontakt-Picker, um einen Kontakt aus den gespeicherten Kontakten auszuwählen
        selectContactButton.setOnClickListener(v -> {
            ContactPicker.pickContact(this);
        });

        // Lädt bereits gespeicherte Notfallkontakte aus der Firebase-Datenbank
        fireBaseHelper.fetchContacts().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ContactModel> contacts = task.getResult();
                contactList.clear();
                contactList.addAll(contacts);
                adapter.notifyDataSetChanged();
                Log.d("eContacts", "Fetched contacts: " + contactList.size());
            } else {
                Log.e("eContacts", "Error fetching contacts", task.getException());
            }
        });
    }

    /**
     * Wird aufgerufen, wenn der Nutzer einen Kontakt über den Kontakt-Picker auswählt.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContactPicker.PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            // Holt den ausgewählten Kontakt aus dem Intent und fügt ihn der Liste hinzu
            ContactModel contact = ContactPicker.getContactData(data, this);
            prepareContact(contact.getName(), contact.getNumber());
            Log.d("eContacts", "Selected contact: " + contact.getName() + " - " + contact.getNumber());
        }
    }

    /**
     * Fügt einen neuen Kontakt hinzu, wenn der Nutzer manuell Name und Nummer eingibt.
     */
    public void addContact(View v) {
        String name = nameInput.getText().toString().trim();
        String number = numberInput.getText().toString().trim();
        prepareContact(name, number);
    }

    /**
     * Fügt den Kontakt zur Firebase-Datenbank hinzu und aktualisiert die Liste in der UI.
     */
    public void prepareContact(String name, String number) {
        if (contactList.size() < 8) { // Maximal 8 Notfallkontakte erlaubt
            fireBaseHelper.addContact(name, number).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String key = task.getResult(); // Holt den Firebase-Schlüssel des gespeicherten Kontakts
                    if (key != null) {
                        contactList.add(new ContactModel(name, number, key));
                        adapter.notifyItemInserted(contactList.size() - 1);
                        Log.d("eContacts", "Contact added: " + name + " - " + number);
                    }
                } else {
                    Toast.makeText(EmergencyContacts.this, "Error adding contact", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Zu viele Kontakte", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Schließt die Activity, wenn der Zurück-Button gedrückt wird.
     */
    public void onBackPressed(View view) {
        finish();
    }
}
