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

public class eContacts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private recyclerAdapter adapter;
    private List<contactModel> contactList;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fireBaseHelper = new FireBaseHelper();
        recyclerView = findViewById(R.id.recycler);
        nameInput = findViewById(R.id.nameInput);
        numberInput = findViewById(R.id.numberInput);
        addButton = findViewById(R.id.addButton);
        selectContactButton = findViewById(R.id.selectContactButton);

        contactList = new ArrayList<>();
        adapter = new recyclerAdapter(contactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        selectContactButton.setOnClickListener(v -> {
            ContactPicker.pickContact(this);
        });

        fireBaseHelper.fetchContacts().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<contactModel> contacts = task.getResult();
                contactList.clear();
                contactList.addAll(contacts);
                adapter.notifyDataSetChanged();
                Log.d("eContacts", "Fetched contacts: " + contactList.size());
            } else {
                Log.e("eContacts", "Error fetching contacts", task.getException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContactPicker.PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            // Hole das contactModel-Objekt direkt aus dem Intent
            contactModel contact = ContactPicker.getContactData(data, this);
            prepareContact(contact.getName(), contact.getNumber());
            Log.d("eContacts", "Selected contact: " + contact.getName() + " - " + contact.getNumber());
        }
    }

    public void addContact(View v) {
        String name = nameInput.getText().toString().trim();
        String number = numberInput.getText().toString().trim();
        prepareContact(name, number);
    }

    public void prepareContact(String name, String number) {
        if (contactList.size() < 8) {
            // Nutzt die neue addContact-Methode, die einen Task<String> zurückgibt (der den generierten Schlüssel liefert)
            fireBaseHelper.addContact(name, number).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String key = task.getResult();
                    if (key != null) {
                        contactList.add(new contactModel(name, number, key));
                        adapter.notifyItemInserted(contactList.size() - 1);
                        Log.d("eContacts", "Contact added: " + name + " - " + number);
                    }
                } else {
                    Toast.makeText(eContacts.this, "Error adding contact", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Zu viele Kontakte", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed(View view) {
        finish();
    }
}
