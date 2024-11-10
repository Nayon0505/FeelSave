package com.example.feelsave;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
    private CountDownTimer countDownTimer;


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
        Log.d("eMessage", "Messagtext: "+ messageHandler.getMessageText());
        emergencyMessage = emergencyMessageInput.getText().toString();

                emergencyMessageInput.setText(messageHandler.getMessageText());



    }

    public void safeInput(View view){
       // fireBaseHelper.readEmergencyMessageFromDB(emergencyMessageInput);
        emergencyMessage = emergencyMessageInput.getText().toString();
        messageHandler.setMessageText(emergencyMessage);



    }

}