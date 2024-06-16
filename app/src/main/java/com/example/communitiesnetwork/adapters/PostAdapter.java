package com.example.communitiesnetwork.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitiesnetwork.R;
import com.example.communitiesnetwork.models.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    Context context;
    public PostAdapter(List<Post> postList,  Context applicationContext) {
        this.postList = postList;
        this.context=applicationContext;

    }
    public void setPostList(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTextView;
        TextView venueTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            venueTextView = itemView.findViewById(R.id.venueTextView);
        }

        public void bind(Post post) {
            titleTextView.setText(post.getTitle());
            descriptionTextView.setText(post.getDescription());
            dateTextView.setText(post.getDate());
            venueTextView.setText(post.getVenue());
        }
    }

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }
}
