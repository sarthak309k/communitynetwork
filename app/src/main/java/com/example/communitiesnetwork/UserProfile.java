package com.example.communitiesnetwork;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {
    private ImageView profilePhoto;
    private TextView username,emailIdUser;
    private Button signOutButton;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfile newInstance(String param1, String param2) {
        UserProfile fragment = new UserProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize views
        profilePhoto = rootView.findViewById(R.id.profile_photo);
        username = rootView.findViewById(R.id.username);
        signOutButton = rootView.findViewById(R.id.sign_out_button);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("email", Context.MODE_PRIVATE);

        // Check if user is authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Set username and email
            String email = currentUser.getEmail();
            username.setText(email); // Set email as username for now

            // Fetch additional user details from Firestore
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(email) // Use the email as the document ID
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Document exists, retrieve user details and update UI
                            String usernameGot = documentSnapshot.getString("username");
                            // Set the retrieved username in the UI
                            username.setText(usernameGot);

                            // Similarly, retrieve other user details as needed
                        } else {
                            // Document does not exist
                            // Handle the case where the user document does not exist
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Log.e(TAG, "Error fetching user document: " + e.getMessage());
                    });
        }

        // Set double-click listener for profile photo
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            long lastClickTime = 0; // Variable to track the last click time
            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis(); // Get current time

                // Check if the time difference between two clicks is less than a threshold
                if (clickTime - lastClickTime < 500) {
                    // If the time difference is small, consider it as a double-click
                    openImagePicker(); // Open image picker
                }
                lastClickTime = clickTime; // Update last click time
            }
        });

        // Set click listener for sign out button
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return rootView;
    }


    private void signOut() {
        // Retrieve user's email from SharedPreferences
        String email = sharedPreferences.getString("email", "");

        // Clear SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Sign out from Firebase Authentication
        mAuth.signOut();

        // Navigate to LoginActivity (or any other appropriate destination)
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish(); // Close current activity
    }


    // Define an ActivityResultLauncher for the image picker
    private ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        // Upload the selected image to Firebase Storage
                      //  uploadImageToFirebaseStorage(result);
                    }
                }
            }
    );

    // In your openImagePicker() method, launch the image picker using the ActivityResultLauncher
    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    // In uploadImageToFirebaseStorage() method, you don't need to handle the result since it's handled in the launcher callback



}