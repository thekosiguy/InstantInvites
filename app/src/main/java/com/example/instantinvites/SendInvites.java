package com.example.instantinvites;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SendInvites extends AppCompatActivity {
    TextView eventName;
    TextView help;
    TextView contactsTitleView;
    TextView contactsList;
    EditText contactToRemove;
    EditText nameOfContact;
    TextView addContact;
    TextView deleteContact;
    TextView deleteAllContacts;
    TextView inviteContacts;

    final String contactsTitle = "Contacts";
    String listOfContacts = "No Contacts.";
    private final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invites);

        eventName = (TextView) findViewById(R.id.eventName);
        contactsTitleView = (TextView) findViewById(R.id.contactsTitle);
        contactsList = (TextView) findViewById(R.id.contactsList);
        nameOfContact = (EditText) findViewById(R.id.mobileNumber);
        addContact = (TextView) findViewById(R.id.addContact);
        deleteContact = (TextView) findViewById(R.id.deleteContact);
        deleteAllContacts = (TextView) findViewById(R.id.deleteContacts);
        inviteContacts = (TextView) findViewById(R.id.inviteContacts);
        help = (TextView) findViewById(R.id.help);

        Intent intent = getIntent();
        String name = intent.getStringExtra("event name");
        eventName.setText(name);

        SpannableString content = new SpannableString(contactsTitle);
        content.setSpan(new UnderlineSpan(), 0, contactsTitle.length(), 0);
        contactsTitleView.setText(content);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean addComma = true;
                if (listOfContacts.equals("No Contacts.")){
                    addComma = false;
                    listOfContacts = "";
                }

                if(addComma == true && (nameOfContact.getText().toString().length() > 0)) {
                    listOfContacts += ", " + nameOfContact.getText().toString().trim();
                    contactsList.setText(listOfContacts);
                }else if (addComma == false && (nameOfContact.getText().toString().length() > 0)){
                    listOfContacts += nameOfContact.getText().toString().trim();
                    contactsList.setText(listOfContacts);
                }
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactsList.getText() == "" || contactsList.getText() == "No Contacts."){
                    Toast.makeText(SendInvites.this, "The contacts list is empty.", Toast.LENGTH_LONG).show();
                }else {
                    createRemoveFromListAlertDialog();
                }
            }
        });

        deleteAllContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOfContacts = "No Contacts.";
                contactsList.setText(listOfContacts);
            }
        });

        inviteContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfContacts.length() < 1 || listOfContacts.equals("No Contacts.")){
                    Toast.makeText(SendInvites.this, "Please add at least 1 contact.", Toast.LENGTH_LONG).show();
                }else{
                    sendSMSmessage();
                }
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendInvites.this, Help.class);
                startActivity(intent);
            }
        });
    }

    public void createRemoveFromListAlertDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Contact To Remove From The List");
        contactToRemove = (EditText) new EditText(this);
        contactToRemove.setHint("Mobile Number");
        contactToRemove.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(contactToRemove);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listOfContacts = listOfContacts.replace(contactToRemove.getText().toString(), "");
                if (listOfContacts.equals("")){
                    listOfContacts = "No Contacts.";
                    contactsList.setText("No Contacts.");
                }else {
                    contactsList.setText(listOfContacts);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void sendSMSmessage(){
        Intent intent = getIntent();
        String name = intent.getStringExtra("event name");
        String description = intent.getStringExtra("event description");
        String venue = intent.getStringExtra("event venue");
        String startTime = intent.getStringExtra("event startTime");
        String endTime = intent.getStringExtra("event endTime");

        String eventMessage = "Hey! you've been invited to the following event..." + "\n\n" + "Event Name: "
                + name + "\n\n" + "Description: " + description + "\n\n" + "Venue: " + venue + "\n\n" +
                "Start Time: " + startTime + "\n\n" + "End Time: " + endTime + "\n\n" + "Please reply in the " +
                "following format: Your Name - Going/Not Going/Might Be There, for e.g. James Ferry - Going" + "\n\n"
                + "This message was sent via the InstantInvites app.";
        try{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(SendInvites.this, "Permission denied so the sms feature can't be used, please grant the app permission to use it.", Toast.LENGTH_SHORT).show();
                    Log.d("permission", "permission denied to SEND_SMS - requesting it");
                    String[] permissions = {Manifest.permission.SEND_SMS};

                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);

                }
            }

            SmsManager smsManager = SmsManager.getDefault();
            String[] arrayOfContacts = listOfContacts.split(", ");
            ArrayList<String> dividedEventMessage = smsManager.divideMessage(eventMessage);

            String mobileNumber;
            for (int i = 0; i < arrayOfContacts.length; i++){
                mobileNumber = arrayOfContacts[i];
                mobileNumber = "+44" + mobileNumber.substring(1);
                smsManager.sendMultipartTextMessage(mobileNumber, null, dividedEventMessage, null, null);
            }
            Toast.makeText(SendInvites.this, "The event details were successfully sent to all contacts in the list!", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(SendInvites.this, "The event details couldn't be sent, please make sure the number(s) you entered are valid and active.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
