package com.example.instantinvites;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventDetails extends AppCompatActivity {
    String eventId, name, description, venue, startTime, endTime;

    TextView eventName;
    TextView eventDescription;
    TextView eventVenue;
    TextView eventStartTime;
    TextView eventEndTime;
    TextView sendInvites;
    TextView updateEvent;
    TextView deleteEvent;
    TextView help;

    String uId = FirebaseAuth.getInstance().getUid();

    private boolean updateEvent(String eventId, String eventName, String eventDescription,
                                String eventVenue, String eventStartTime, String eventEndTime){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uId).child("Events").child(eventId);
        newEvent event = new newEvent(eventId, eventName, eventDescription, eventVenue, eventStartTime, eventEndTime);
        databaseReference.setValue(event);
        Toast.makeText(this, "The event has been updated.", Toast.LENGTH_LONG).show();
        return true;
    }

    private void deleteEvent(String eventId){
        DatabaseReference drEvents = FirebaseDatabase.getInstance().getReference(uId).child("Events").child(eventId);
        drEvents.removeValue();
        Toast.makeText(EventDetails.this, "The event has been deleted.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(EventDetails.this, EventsList.class);
        startActivity(intent);
    }

    private void showUpdateDialog(final String eventId, String eventName, String eventDescription, String eventVenue,
    String eventStartTime, String eventEndTime){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_event, null);
        dialogBuilder.setView(dialogView);

        final EditText editEventName = (EditText) dialogView.findViewById(R.id.editEventName);
        final EditText editEventDescription = (EditText) dialogView.findViewById(R.id.editEventDescription);
        final EditText editEventVenue = (EditText) dialogView.findViewById(R.id.editEventVenue);
        final EditText editEventStartTime = (EditText) dialogView.findViewById(R.id.editEventStartTime);
        final EditText editEventEndTime = (EditText) dialogView.findViewById(R.id.editEventEndTime);
        final Button updateButton = (Button) dialogView.findViewById(R.id.updateButton);

        dialogBuilder.setTitle("Updating " + eventName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = editEventName.getText().toString().trim();
                String eventDescription = editEventDescription.getText().toString().trim();
                String eventVenue = editEventVenue.getText().toString().trim();
                String eventStartTime = editEventStartTime.getText().toString().trim();
                String eventEndTime = editEventEndTime.getText().toString().trim();

                if (TextUtils.isEmpty(eventName)){
                    eventName = name;
                }

                if (TextUtils.isEmpty(eventDescription)){
                    eventDescription = description;
                }

                if (TextUtils.isEmpty(eventVenue)){
                    eventVenue = venue;
                }

                if (TextUtils.isEmpty(eventStartTime)){
                    eventStartTime = startTime;
                }

                if (TextUtils.isEmpty(eventEndTime)){
                    eventEndTime = endTime;
                }

                updateEvent(eventId, eventName, eventDescription, eventVenue, eventStartTime, eventEndTime);
                alertDialog.dismiss();
            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        eventName = (TextView) findViewById(R.id.eventName);
        eventDescription = (TextView) findViewById(R.id.eventDescription);
        eventVenue = (TextView) findViewById(R.id.eventVenue);
        eventStartTime = (TextView) findViewById(R.id.eventStartTime);
        eventEndTime = (TextView) findViewById(R.id.eventEndTime);
        sendInvites = (TextView) findViewById(R.id.sendInvites);
        updateEvent = (TextView) findViewById(R.id.updateEvent);
        deleteEvent = (TextView) findViewById(R.id.deleteEvent);
        help = (TextView) findViewById(R.id.help);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("event id");
        name = intent.getStringExtra("event name");
        description = intent.getStringExtra("event description");
        venue = intent.getStringExtra("event venue");
        startTime = intent.getStringExtra("event startTime");
        endTime = intent.getStringExtra("event endTime");

        eventName.setText(name);
        eventDescription.setText(description);
        eventVenue.setText("Venue: " + venue);
        eventStartTime.setText("Start Time: " + startTime);
        eventEndTime.setText("End Time: " + endTime);

        sendInvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetails.this, SendInvites.class);
                intent.putExtra("event name", name);
                intent.putExtra("event description", description);
                intent.putExtra("event venue", venue);
                intent.putExtra("event startTime", startTime);
                intent.putExtra("event endTime", endTime);
                startActivity(intent);
            }
        });

        updateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog(eventId, name, description, venue, startTime, endTime);
            }
        });

        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent(eventId);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetails.this, Help.class);
                startActivity(intent);
            }
        });
    }
}
