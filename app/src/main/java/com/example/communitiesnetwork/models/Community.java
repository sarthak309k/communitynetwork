package com.example.communitiesnetwork.models;

import java.util.Map;

public class Community {
    String communityId;
    String icon;
    String name;
    String description;
    String leader;

    private Map<String, Object> communityMembers; // New field to store community members

    // Constructors, getters, and setters

    public Map<String, Object> getCommunityMembers() {
        return communityMembers;
    }

    public void setCommunityMembers(Map<String, Object> communityMembers) {
        this.communityMembers = communityMembers;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommunityCode() {
        return communityCode;
    }

    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
    }

    public Community(String communityId, String icon, String name, String description, String communityCode,String leader) {
        this.communityId = communityId;
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.communityCode = communityCode;
        this.leader=leader;
    }

    public Community() {
        // Default constructor required for Firestore
    }

    String communityCode;

}
