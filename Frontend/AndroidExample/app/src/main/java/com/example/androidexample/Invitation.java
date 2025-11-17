package com.example.androidexample;

// Might use this in the future to organize things but currently not in use

public class Invitation {
    private int id;
    private String userName;
    private String dateCreated;

    public Invitation(int id, String userName, String dateCreated) {
        this.id = id;
        this.userName = userName;
        this.dateCreated = dateCreated;
    }

    public int getId() { return id; }
    public String getUserName() { return userName; }
    public String getDateCreated() { return dateCreated; }
}
