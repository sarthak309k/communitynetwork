package com.example.communitiesnetwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.communitiesnetwork.adapters.CommunityMembersAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// CommunityInfo.java
public class CommunityInfo extends AppCompatActivity {

    private RecyclerView recyclerViewMembers;
    private String communityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);

        // Receive the community code passed from the previous activity
        Intent intent = getIntent();

        communityCode = intent.getStringExtra("community_id");
        System.out.println("Community Code"+communityCode);
        // Initialize RecyclerView and set layout manager
        recyclerViewMembers = findViewById(R.id.recycler_view_members);
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve community details from the database and populate views
        retrieveCommunityDetails();
    }

    private void retrieveCommunityDetails() {
        // Reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println("Community Code"+communityCode);
        // Query to fetch community details using the community code
        DocumentReference communityRef = db.collection("communities").document(communityCode);

        // Retrieve community details from Firestore
        communityRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Community document exists, extract details
                String communityName = documentSnapshot.getString("name");
                String leader = documentSnapshot.getString("leader");

                // Update UI with community details
                TextView communityNameTextView = findViewById(R.id.community_name_text);
                communityNameTextView.setText("Community Name: " + communityName);

                TextView leaderTextView = findViewById(R.id.leader_text);
                leaderTextView.setText("Leader: " + leader);

                TextView communityCodeTextView=findViewById(R.id.community_code_text);
                communityCodeTextView.setText("Community Code: "+communityCode);
                // If there are members in the community, fetch and display them
                if (documentSnapshot.contains("communityMembers")) {
                    Map<String, Boolean> communityMembers = (Map<String, Boolean>) documentSnapshot.get("communityMembers");
                    displayMembers


                            (communityMembers);
                }
            } else {
                // Community document does not exist
                Log.d("CommunityInfo", "No such community");
            }
        }).addOnFailureListener(e -> {
            // Failed to fetch community details
            Log.e("CommunityInfo", "Error fetching community details: " + e.getMessage());
        });
    }

    private void displayMembers(Map<String, Boolean> communityMembers) {
        // Extract keys from the communityMembers map
        List<String> membersList = new ArrayList<>(communityMembers.keySet());

        // Create and set the adapter for the RecyclerView
        CommunityMembersAdapter adapter = new CommunityMembersAdapter(membersList);
        recyclerViewMembers.setAdapter(adapter);
    }

}

