package com.example.communitiesnetwork.models;

import java.util.List;

public class Post {
    private String title;
    private String description;
    private String date;
    private List<String> attendanceList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String venue;

    private String id;

    // Required empty constructor for Firestore deserialization
    public Post() {
    }

    public Post(String title, String description, String date, String venue) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.venue = venue;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public List<String> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<String> attendanceList) {
        this.attendanceList = attendanceList;
    }
}
