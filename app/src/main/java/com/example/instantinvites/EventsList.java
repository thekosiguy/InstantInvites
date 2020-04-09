package com.example.instantinvites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsList extends AppCompatActivity {
    TextView addEvent;
    TextView deleteAllEvents;
    TextView placesOfInterest;
    TextView compass;
    TextView deleteAccount;
    TextView help;
    Button logoutButton;

    DatabaseReference databaseEventDetails;
    ListView listViewEvents;
    List<newEvent> eventsList;

    String uId = FirebaseAuth.getInstance().getUid();

    private void deleteEvents() {
        if (eventsList.size() != 0) {
            DatabaseReference drEvents = FirebaseDatabase.getInstance().getReference(uId).child("Events");
            drEvents.removeValue();
            Toast.makeText(EventsList.this, "All events have been deleted.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(EventsList.this, "The events list is empty, an event hasn't been added to it.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        databaseEventDetails = FirebaseDatabase.getInstance().getReference(uId);

        addEvent = (TextView) findViewById(R.id.addEvent);
        deleteAllEvents = (TextView) findViewById(R.id.deleteAllEvents);
        placesOfInterest = (TextView) findViewById(R.id.locatePlacesButton);
        compass = (TextView) findViewById(R.id.compass);
        deleteAccount = (TextView) findViewById(R.id.deleteAccount);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        help = (TextView) findViewById(R.id.help);

        listViewEvents = (ListView) findViewById(R.id.listViewEvents);
        eventsList = new ArrayList<>();

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsList.this, AddEvent.class);
                startActivity(intent);
            }
        });

        deleteAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvents();
            }
        });

        placesOfInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsList.this, NearbyPlacesOfInterest.class);
                startActivity(intent);
            }
        });

        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsList.this, Compass.class);
                startActivity(intent);
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(EventsList.this, "Your account has been successfully deleted.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(EventsList.this, SignUp.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(EventsList.this, "An error occurred, please log out then log back in and try again or try again later.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(EventsList.this, Login.class);
                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsList.this, Help.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseEventDetails = FirebaseDatabase.getInstance().getReference(uId).child("Events");
        databaseEventDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();

                for(DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                    newEvent event = eventSnapshot.getValue(newEvent.class);
                    eventsList.add(event);
                }

                EventsListAdapter adapter = new EventsListAdapter(EventsList.this, eventsList);
                listViewEvents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
