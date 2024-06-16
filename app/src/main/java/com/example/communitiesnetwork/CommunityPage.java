package com.example.communitiesnetwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.communitiesnetwork.adapters.PostAdapter;
import com.example.communitiesnetwork.models.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityPage extends AppCompatActivity  {
    TextView tv;
    Intent it;
    boolean isLeader;
    RecyclerView recyclerViewPost;
    private FirebaseAuth mAuth;
    private String communityCode;
    FirebaseFirestore db;
    private FrameLayout mainContentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_page);
        Toolbar toolbar=findViewById(R.id.toolbar);
        recyclerViewPost=findViewById(R.id.recycler_view_posts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewPost.setLayoutManager(layoutManager);
        it = getIntent();
        db=FirebaseFirestore.getInstance();
        toolbar.setTitle(it.getStringExtra("community_name"));
        //tv.setText("Community id:-" + it.getStringExtra("community_id") + "\n IsLeader:-" + it.getBooleanExtra("isLeader", false));
        isLeader = it.getBooleanExtra("isLeader", false);
        communityCode=it.getStringExtra("community_id");
        // Get the floating action button
        FloatingActionButton fab = findViewById(R.id.fab_create_post);
        if (isLeader) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Open dialog to create community event post
                    showCreatePostDialog();

                }
            });
        } else {
            fab.setVisibility(View.GONE);

        }

        displayPosts();
        mainContentContainer = findViewById(R.id.main_content_container);

        // Setup info icon click listener
        ImageView infoIcon = findViewById(R.id.info_icon);
        infoIcon.setOnClickListener(v -> onInfoIconClicked());
    }

    private void onInfoIconClicked() {
        Intent intent = new Intent(this, CommunityInfo.class);
        intent.putExtra("community_id",communityCode);
        startActivity(intent);
    }


    private void showCreatePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Community Event Post");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_post, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.et_title);
        EditText etDescription = dialogView.findViewById(R.id.et_description);
        EditText etVenue = dialogView.findViewById(R.id.et_venue);

        // Date picker
        DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
        datePicker.setCalendarViewShown(false); // Hide the calendar view

        // Time picker
        TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true); // Set to 24-hour format

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String date = getDateFromDatePicker(datePicker, timePicker); // Get selected date and time
                String venue = etVenue.getText().toString().trim();

                if (title.isEmpty() || description.isEmpty() || date.isEmpty() || venue.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                createPost(title, description, date, venue);
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

    private String getDateFromDatePicker(DatePicker datePicker, TimePicker timePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Month starts from 0
        int year = datePicker.getYear();

        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        return year + "-" + month + "-" + day + " " + hour + ":" + minute;
    }



    private void createPost(String title, String description, String date, String venue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new Post object with the provided details
        Post post = new Post(title, description, date, venue);

        // Add the post to Firestore under the specific community using communityCode as the document ID
        db.collection("communities").document(communityCode)
                .collection("event_posts").document()
                .set(post)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Post created successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to create post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void displayPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the posts collection for the specific community using communityCode as the document ID
        db.collection("communities").document(communityCode)
                .collection("event_posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Create a list to hold the Post objects
                    List<Post> postList = new ArrayList<>();

                    // Iterate through the documents in the query result
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Convert each document to a Post object
                        Post post = document.toObject(Post.class);
                        // Add the Post object to the list
                        postList.add(post);
                    }

                    // Pass the list of Post objects to the RecyclerView adapter
                    PostAdapter adapter = new PostAdapter(postList,getApplicationContext());
                    adapter.notifyDataSetChanged();
                    recyclerViewPost.setAdapter(adapter);

                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur during the query
                    Log.e("CommunityPage", "Error retrieving posts: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Error retrieving posts", Toast.LENGTH_SHORT).show();
                });
    }




    private void updatePostAttendance(Post post) {
        // Get the current user ID (you may need to replace this with your actual implementation to get the user ID)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Construct a reference to the specific post document in Firestore
        DocumentReference postRef = db.collection("communities").document(communityCode)
                .collection("event_posts").document(post.getId());

        // Use a transaction to ensure data consistency
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(postRef);
            List<String> attendanceList = snapshot.toObject(Post.class).getAttendanceList();

            // If the attendance list is null, initialize it
            if (attendanceList == null) {
                attendanceList = new ArrayList<>();
            }

            // Check if the user is already in the attendance list
            if (attendanceList.contains(userId)) {
                // If user already attended, remove from attendance list
                attendanceList.remove(userId);
            } else {
                // Add the user to the attendance list
                attendanceList.add(userId);
            }

            // Update the attendance list in the post document
            transaction.update(postRef, "attendanceList", attendanceList);

            // Return null to indicate that the transaction completed successfully
            return null;
        }).addOnSuccessListener(aVoid -> {
            // Attendance updated successfully
            Toast.makeText(getApplicationContext(), "Attendance updated", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Failed to update attendance
            Log.e("CommunityPage", "Failed to update attendance: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Failed to update attendance", Toast.LENGTH_SHORT).show();
        });
    }



}
    // The rest of your code...

