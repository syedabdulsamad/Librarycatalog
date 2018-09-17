package com.example.abdulsamad.librarycatalog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import Model.User;

import static android.widget.Toast.LENGTH_SHORT;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;


    private TextView firstNameTextField = null;
    private EditText lastNameTextField = null;
    private EditText emailTextField = null;
    private EditText passwordTextField = null;
    private EditText aimsIdTextField = null;

    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private String password = null;
    private String aimsId = null;

    private FirebaseUser firebaseUser = null;
    private Button registerButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();


        setUIElements();
        setOnClickListener();

    }


    private void setUIElements() {

        firstNameTextField = (EditText) findViewById(R.id.firstNameTextField);
        lastNameTextField = (EditText) findViewById(R.id.lastNameTextField);
        emailTextField = (EditText) findViewById(R.id.emailTextField);
        passwordTextField = (EditText) findViewById(R.id.passwordTextField);
        aimsIdTextField = (EditText) findViewById(R.id.aimsIdTextField);
        registerButton = (Button) findViewById(R.id.registerButton);
    }

    private void setOnClickListener() {
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }


    private void registerUser() {

        firstName = firstNameTextField.getText().toString();
        lastName = lastNameTextField.getText().toString();
        email = emailTextField.getText().toString();
        aimsId = aimsIdTextField.getText().toString();
        password = passwordTextField.getText().toString();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("USER", "Task Successful");
                    if (auth.getCurrentUser() != null) {
                        checkExistingUser();
                    } else {
                        Log.d("USER", "No current user.");
                    }
                } else {

                    Log.d("USER", "ELSE " + task.getException().getMessage());
                }
            }
        });
    }


    public void checkExistingUser() {

        databaseRef = FirebaseDatabase.getInstance().getReference("LibraryCatalog");
        Query query = databaseRef.child("users").child(aimsId);
        // Query query = databaseRef.child("users").orderByChild(aimsId).equalTo(aimsId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("USER", dataSnapshot.getKey());
                Log.d("USER", dataSnapshot.toString());
                Log.d("USER", dataSnapshot.getChildren().toString());


                if (dataSnapshot.getValue() != null) {
                    Log.d("USER", "Snapshot value is not null");

                    Toast.makeText(getApplicationContext(), "User already exists.", LENGTH_SHORT).show();
                } else {
                    Log.d("USER", "Snapshot value is NULL");
                    Toast.makeText(getApplicationContext(), "User does not exists.", LENGTH_SHORT).show();
                    setupUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupUser() {

        User u = new User(firstName, lastName, aimsId, email);
        databaseRef.child("users").child(aimsId).setValue(u).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("USER", "USER CREATED");
                        Toast.makeText(getApplicationContext(), "User created.", LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("USER", "USER FAILED");
                Toast.makeText(getApplicationContext(), "User setting failed", LENGTH_SHORT).show();
            }
        });
    }
}