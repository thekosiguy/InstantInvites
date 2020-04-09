package com.example.instantinvites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEvent extends AppCompatActivity {
    EditText editTextName;
    EditText editTextDescription;
    EditText editTextVenue;
    EditText editStartTime;
    EditText editEndTime;
    TextView help;
    Button addEventButton;

    DatabaseReference databaseEventDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        databaseEventDetails = FirebaseDatabase.getInstance().getReference();

        editTextName = findViewById(R.id.nameOfEvent);
        editTextDescription = findViewById(R.id.description);
        editTextVenue = findViewById(R.id.venue);
        editStartTime = findViewById(R.id.startTime);
        editEndTime = findViewById(R.id.endTime);
        help = findViewById(R.id.help);
        addEventButton = findViewById(R.id.addEventButton);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEvent();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEvent.this, Help.class);
                startActivity(intent);
            }
        });
    }

    private void newEvent(){
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String venue = editTextVenue.getText().toString().trim();
        String startTime = editStartTime.getText().toString().trim();
        String endTime = editEndTime.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(venue) &&
            !TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)){
            String eventId = databaseEventDetails.push().getKey();
            String userId = FirebaseAuth.getInstance().getUid();
            newEvent event = new newEvent(eventId, name, description, venue, startTime, endTime);
            databaseEventDetails.child(userId).child("Events").child(eventId).setValue(event);
            Toast.makeText(AddEvent.this, "Event successfully added!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(AddEvent.this, "Please make sure you've entered data into each text field.", Toast.LENGTH_LONG).show();
        }
    }
}
