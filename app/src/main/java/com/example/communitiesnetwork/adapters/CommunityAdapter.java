package com.example.communitiesnetwork.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitiesnetwork.R;
import com.example.communitiesnetwork.models.Community;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private List<Community> communityList;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Community community);
    }

    public CommunityAdapter(List<Community> communityList, OnItemClickListener listener,Context context) {
        this.context=context;
        this.communityList = communityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        return new CommunityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Community community = communityList.get(position);
        holder.bind(community);
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder {
        private TextView communityNameTextView;
        private ImageView communityIconImageView;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            communityNameTextView = itemView.findViewById(R.id.community_name);
            communityIconImageView = itemView.findViewById(R.id.community_icon);
        }

        public void bind(final Community community) {
            communityNameTextView.setText(community.getName());

            // Set community icon if available
            // communityIconImageView.setImageDrawable(...);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(community);
                    }
                }
            });
        }
    }
}

