package com.example.communitiesnetwork.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitiesnetwork.R;

import java.util.List;

// CommunityMembersAdapter.java
public class CommunityMembersAdapter extends RecyclerView.Adapter<CommunityMembersAdapter.MemberViewHolder> {

    private List<String> membersList;

    public CommunityMembersAdapter(List<String> membersList) {
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String memberName = membersList.get(position);
        holder.memberNameTextView.setText(memberName);
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView memberNameTextView;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberNameTextView = itemView.findViewById(R.id.member_name_text);
        }
    }
}
