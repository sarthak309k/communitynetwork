package com.example.communitiesnetwork;

import static androidx.constraintlayout.widget.StateSet.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.communitiesnetwork.adapters.PostAdapter;
import com.example.communitiesnetwork.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventAlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventAlertFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerViewEvent;
    private PostAdapter postAdapter;

    public EventAlertFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventAlertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventAlertFragment newInstance(String param1, String param2) {
        EventAlertFragment fragment = new EventAlertFragment();
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
        View view = inflater.inflate(R.layout.fragment_event_alert, container, false);

        recyclerViewEvent = view.findViewById(R.id.recycler_view_event);
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve event posts from Firestore and set the adapter when the task completes
        getEventPostsFromFirestore().addOnSuccessListener(eventPosts -> {
            // Create the PostAdapter with the fetched event posts
            postAdapter = new PostAdapter(eventPosts,getContext());

            // Set the adapter for the RecyclerView
            recyclerViewEvent.setAdapter(postAdapter);
        }).addOnFailureListener(e -> {
            // Handle failure to fetch event posts
            Log.e(TAG, "Failed to fetch event posts: " + e.getMessage());
        });

        return view;
    }


    // Method to retrieve event posts from Firestore
    private Task<List<Post>> getEventPostsFromFirestore() {
        // Fetch event posts from Firestore
        return FirebaseFirestore.getInstance()
                .collection("communities")
                .get()
                .continueWithTask(task -> {
                    List<Task<List<Post>>> tasks = new ArrayList<>();
                    for (DocumentSnapshot communitySnapshot : task.getResult()) {
                        String communityId = communitySnapshot.getId();
                        Task<List<Post>> communityPostsTask = FirebaseFirestore.getInstance()
                                .collection("communities").document(communityId)
                                .collection("event_posts")
                                .get()
                                .continueWith(postQueryDocumentSnapshots -> {
                                    List<Post> communityPosts = new ArrayList<>();
                                    for (DocumentSnapshot documentSnapshot : postQueryDocumentSnapshots.getResult()) {
                                        // Convert each document to a Post object and add it to the communityPosts list
                                        Post post = documentSnapshot.toObject(Post.class);
                                        communityPosts.add(post);
                                    }
                                    return communityPosts;
                                });
                        tasks.add(communityPostsTask);
                    }
                    return Tasks.whenAllSuccess(tasks);
                })
                .continueWith(task -> {
                    List<Post> eventPosts = new ArrayList<>();
                    for (Object communityPosts : task.getResult()) {
                        eventPosts.addAll((Collection<? extends Post>) communityPosts);
                    }
                    // Sort the eventPosts list based on the date
                    Collections.sort(eventPosts, (post1, post2) -> post2.getDate().compareTo(post1.getDate()));
                    return eventPosts;
                });
    }




}
