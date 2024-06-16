package com.example.communitiesnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.communitiesnetwork.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText fname, lname, emailId, userName, userPassword;
    Button signup;
    FirebaseAuth mAuth;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fname = findViewById(R.id.firstname);
        lname = findViewById(R.id.Surname);
        emailId = findViewById(R.id.emailid);
        userName = findViewById(R.id.usrname);
        userPassword = findViewById(R.id.user_password);
        signup = findViewById(R.id.register);

        mAuth = FirebaseAuth.getInstance();
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailId.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success, save email to SharedPreferences
                                    saveEmailLocally(email);

                                    // Store additional user data in Firestore
                                    String firstName = fname.getText().toString().trim();
                                    String lastName = lname.getText().toString().trim();
                                    String username = userName.getText().toString().trim();
                                    String name = firstName + " " + lastName;

                                    // Create a HashMap to represent the user data
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("name", name);
                                    userData.put("email", email);
                                    userData.put("username", username);

                                    // Get a reference to the Firestore database
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    // Set the user data under a document with the username as the document ID
                                    db.collection("users")
                                            .document(email)
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SignupActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();

                                                    // Navigate to the next activity
                                                    Intent intent = new Intent(SignupActivity.this, HomePage.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // If sign up fails, display a message to the user.
                                                    Toast.makeText(SignupActivity.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // If sign up fails, display a message to the user.
                                    Toast.makeText(SignupActivity.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
      }

            private void saveEmailLocally(String email) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("email", email);
                editor.apply();
            }

    }
