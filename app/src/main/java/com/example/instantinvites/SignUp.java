package com.example.instantinvites;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
        EditText emailEditText;
        EditText passwordEditText;
        TextView login;
        TextView aboutText;
        TextView help;
        Button signUpButton;
        FirebaseAuth mFirebaseAuth;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            mFirebaseAuth = FirebaseAuth.getInstance();
            emailEditText = findViewById(R.id.email);
            passwordEditText = findViewById(R.id.password);
            login = findViewById(R.id.login);
            aboutText = findViewById(R.id.aboutUs);
            help = findViewById(R.id.help);
            signUpButton = findViewById(R.id.signUpButton);

            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    if (email.isEmpty() && password.isEmpty()){
                        Toast.makeText(SignUp.this, "Both fields are empty.", Toast.LENGTH_LONG).show();
                    }
                    else if (email.isEmpty()) {
                        emailEditText.setError("Please enter a valid email address");
                        emailEditText.requestFocus();
                    }
                    else if (password.isEmpty()){
                        passwordEditText.setError("Please enter your password");
                        passwordEditText.requestFocus();
                    }
                    else if (!(email.isEmpty() && password.isEmpty())){
                        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(SignUp.this, "Sign up unsuccessful, please try again.", Toast.LENGTH_LONG).show();
                                }else {
                                    Intent intent = new Intent(SignUp.this, EventsList.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(SignUp.this, "Error occurred! please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    Intent intent = new Intent(SignUp.this, Login.class);
                    startActivity(intent);
                }
            });

            aboutText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SignUp.this, AboutUs.class);
                    startActivity(intent);
                }
            });

            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SignUp.this, Help.class);
                    startActivity(intent);
                }
            });
        }
}
