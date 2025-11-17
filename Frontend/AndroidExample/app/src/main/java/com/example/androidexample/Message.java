package com.example.androidexample;

public class Message {
    private String username;
    private String text;
    private String time;
    private String date;
    private int profileRes;

    public Message(String username, String text, String time, String date, int profileRes) {
        this.username = username;
        this.text = text;
        this.time = time;
        this.date = date;
        this.profileRes = profileRes;
    }

    public String getUsername() { return username; }
    public String getText() { return text; }
    public String getTime() { return time; }
    public String getDate() { return date; }
    public int getProfileRes() { return profileRes; }
}