package com.example.feelsave;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private String name;
    private String number;
    private FireBaseHelper fireBaseHelper;

    private Button selectContactButton;

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
            // Starte den Kontakt-Picker
            ContactPicker.pickContact(this);
        });

        fireBaseHelper.fetchContacts(new FireBaseHelper.FirebaseCallback() {
            @Override
            public void onContactsFetched(List<contactModel> contacts) {
                contactList.clear();  // Clear the old list
                contactList.addAll(contacts);  // Add the updated list
                adapter.notifyDataSetChanged();  // Refresh the RecyclerView
                Log.d("Econtacts","Fetching..."+ contactList.size());
            }

            @Override
            public void onEmergencyMessageFetched(String emergencyMessage) {

            }

            @Override
            public void onLocationFetched(List<LocationModel> location) {

            }


        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContactPicker.PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            contactModel contact = ContactPicker.getContactData(data, this);

            prepareContact(contact.getName(),contact.getNumber());
            Log.d("MainActivity", "Kontakt ausgewählt: " + contact.getName() + " - " + contact.getNumber());
        }
    }

    public void addContact(View v){
        name = nameInput.getText().toString();
        number = numberInput.getText().toString();

        prepareContact(name, number);

    }

    public void prepareContact(String name, String number) {
        int totalContacts = contactList.size();
        if(totalContacts<8) {
            String key = fireBaseHelper.addContactsToDB(name, number);

            // Zur lokalen Liste hinzufügen
            if (key != null) {
                contactList.add(new contactModel(name, number, key));
                adapter.notifyItemInserted(contactList.size() - 1);
                Log.d("eContacts", "Contact added: " + name + " " + number);
            }
        }
        else{
            Toast.makeText(this,"Zu viele Kontakte",Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed(View view) {
        finish();
    }
}