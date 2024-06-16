package com.example.communitiesnetwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.communitiesnetwork.adapters.CommunityAdapter;
import com.example.communitiesnetwork.models.Community;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityList extends Fragment implements CommunityAdapter.OnItemClickListener{
    private RecyclerView recyclerView;
    private CommunityAdapter adapter;
    private List<Community> communityList;
    private FirebaseFirestore db;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommunityList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityList.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityList newInstance(String param1, String param2) {
        CommunityList fragment = new CommunityList();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community_list, container, false);

        // Find the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_communities);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the communities collection
        db.collection("communities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Create a list to hold community objects
                    List<Community> communityList = new ArrayList<>();

                    // Iterate through the documents in the query result
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Convert each document to a Community object
                        Community community = document.toObject(Community.class);
                        // Add the community object to the list
                        communityList.add(community);
                    }

                    // Create a new CommunityAdapter with the communityList
                    CommunityAdapter adapter = new CommunityAdapter(communityList,this::onItemClick,getContext());
                    // Set the adapter for the RecyclerView
                    recyclerView.setAdapter(adapter);
                    // Set the layout manager for the RecyclerView
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur during the query
                    Log.e("CommunityListFragment", "Error retrieving communities: " + e.getMessage());
                    Toast.makeText(getContext(), "Error retrieving communities", Toast.LENGTH_SHORT).show();
                });

        // Find the FloatingActionButton
        FloatingActionButton fab = view.findViewById(R.id.fab);

        // Set OnClickListener for the FloatingActionButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PopupMenu
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                popupMenu.getMenuInflater().inflate(R.menu.fab_menu, popupMenu.getMenu());

                // Set click listener for each menu item
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.crt_community)
                        {
                            showCreateCommunityDialog();
                        }
                        else if(item.getItemId()==R.id.jt_community)
                        {
                            showJoinCommunityDialog();
                        }
                        return true;
                    }
                });

                // Show the PopupMenu
                popupMenu.show();
            }
        });
        queryAndFilterCommunities(recyclerView);
        return view;
    }

    private void showCreateCommunityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create Community");

        // Set up the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_community, null);
        builder.setView(dialogView);

        // Set up the EditText fields
        final EditText etCommunityIcon = dialogView.findViewById(R.id.et_community_icon);
        final EditText etCommunityName = dialogView.findViewById(R.id.et_community_name);
        final EditText etCommunityDescription = dialogView.findViewById(R.id.et_community_description);

        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the input from EditText fields
                String communityIcon = etCommunityIcon.getText().toString().trim();
                String communityName = etCommunityName.getText().toString().trim();
                String communityDescription = etCommunityDescription.getText().toString().trim();
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                String leader=firebaseAuth.getCurrentUser().getEmail();
                // Save data to Firebase database
                saveCommunityToDatabase(communityIcon, communityName, communityDescription,leader);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel button clicked, do nothing
            }
        });

        // Show the dialog
        builder.create().show();
    }

    private void saveCommunityToDatabase(String icon, String name, String description, String leader) {
        // Generate a unique code for the community (8 characters, alphanumeric)
        String communityCode = generateCommunityCode();

        // Save community data to Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("communities");
        String communityId = databaseReference.push().getKey();
        Community community = new Community(communityId, icon, name, description, communityCode, leader);
        databaseReference.child(communityCode).setValue(community);

        // Save community data to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> communityData = new HashMap<>();
        communityData.put("icon", icon);
        communityData.put("name", name);
        communityData.put("leader", leader);
        communityData.put("description", description);
        communityData.put("communityCode", communityCode);

        db.collection("communities").document(communityCode).set(communityData);
        Intent intent = new Intent(getActivity(), CommunityPage.class);
        intent.putExtra("community_id", communityCode);
        intent.putExtra("isLeader", true); // Check if user is leader
        intent.putExtra("community_name",name);
        startActivity(intent);
    }


    private String generateCommunityCode() {
        // Generate a random alphanumeric code of length 8
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 8; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }



    private void showJoinCommunityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Join Community");

        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setHint("Enter community code");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String communityCode = input.getText().toString();
                joinCommunity(communityCode);
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

    private void joinCommunity(String communityCode) {
        // Get the current user's email
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Remove the '@gmail.com'
        String userId = userEmail.replace("@gmail.com", "");

        // Query the communities collection to find the community with the entered code
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("communities")
                .whereEqualTo("communityCode", communityCode)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Community with the entered code exists
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String communityId = document.getId();

                        // Update the community's member list in Firestore
                        db.collection("communities").document(communityId)
                                .update("communityMembers." + userId, true)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getActivity(), "Joined community successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), CommunityPage.class);
                                    intent.putExtra("community_id", communityCode);
                                    intent.putExtra("isLeader", document.getString("leader").equals(userEmail)); // Check if user is leader
                                    intent.putExtra("community_name",document.getString("name"));
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Failed to join community: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                        // Update the community's member list in Realtime Database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("communities").child(communityId);
                        databaseReference.child("communityMembers").child(userId).setValue(true)
                                .addOnSuccessListener(aVoid -> {

                                    // Success
                                })
                                .addOnFailureListener(e -> {
                                    // Failure
                                });
                    } else {
                        // No community found with the entered code
                        Toast.makeText(getActivity(), "Community not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Failed to query communities collection
                    Toast.makeText(getActivity(), "Error joining community: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void queryAndFilterCommunities(RecyclerView recyclerView) {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the communities collection
        db.collection("communities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Create a list to hold filtered community objects
                    List<Community> filteredCommunityList = new ArrayList<>();

                    // Iterate through the documents in the query result
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Convert each document to a Community object
                        Community community = document.toObject(Community.class);
                        // Check if the user is a member of this community
                        if (isUserMemberOfCommunity(community)) {
                            // Add the community object to the filtered list
                            filteredCommunityList.add(community);
                        }
                    }

                    // Create a new CommunityAdapter with the filteredCommunityList
                    CommunityAdapter adapter = new CommunityAdapter(filteredCommunityList, this::onItemClick, getContext());
                    // Set the adapter for the RecyclerView
                    recyclerView.setAdapter(adapter);
                    // Set the layout manager for the RecyclerView
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur during the query
                    Log.e("CommunityListFragment", "Error retrieving communities: " + e.getMessage());
                    Toast.makeText(getContext(), "Error retrieving communities", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isUserMemberOfCommunity(Community community) {
        // Get the current user's ID
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String userId = userEmail.replace("@gmail.com", "");
        System.out.println(""+userId);
        // Check if the user is the leader of the community
        if (userEmail.equals(community.getLeader())) {
            return true; // User is the leader
        }

        // Check if the user is a member of the community
        Map<String, Object> communityMembers = community.getCommunityMembers();
        if (communityMembers != null && communityMembers.containsKey(userId)) {
            return true; // User is a member
        }

        return false; // User is neither the leader nor a member
    }


    private void retrieveCommunityData() {
        db.collection("communities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        communityList.clear(); // Clear the list before adding new data
                        for (DocumentSnapshot document : task.getResult()) {
                            Community community = document.toObject(Community.class);
                            communityList.add(community);
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter that data set has changed
                    } else {
                        Log.d("CommunityListFragment", "Error getting community documents: ", task.getException());
                    }
                });
    }

    @Override
    public void onItemClick(Community community) {
        System.out.println(community.getCommunityCode());
        Intent intent = new Intent(getActivity(), CommunityPage.class);
        // Pass any necessary data to the activity using intent extras
        intent.putExtra("community_id", community.getCommunityCode());

        // Add user role information (leader or member) to the intent extras
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        boolean isLeader = community.getLeader().equals(userEmail);
        intent.putExtra("isLeader", isLeader);
        intent.putExtra("community_name",community.getName());
        startActivity(intent);
    }

}