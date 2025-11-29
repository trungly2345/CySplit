package com.example.androidexample;

/**
 * Represents a user invitation within the app.
 * <p>
 * Currently not actively used in the application, but may be utilized in the future
 * to manage or display invitations sent to or from users.
 * </p>
 */
public class Invitation {

    /** Unique identifier for the invitation. */
    private int id;

    /** Username of the user associated with the invitation. */
    private String userName;

    /** Date the invitation was created, in string format. */
    private String dateCreated;

    /**
     * Constructs a new Invitation with the specified ID, username, and creation date.
     *
     * @param id          Unique identifier for the invitation
     * @param userName    Username of the associated user
     * @param dateCreated Date the invitation was created
     */
    public Invitation(int id, String userName, String dateCreated) {
        this.id = id;
        this.userName = userName;
        this.dateCreated = dateCreated;
    }

    /**
     * Returns the ID of the invitation.
     *
     * @return Invitation ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the username associated with the invitation.
     *
     * @return Username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the date when the invitation was created.
     *
     * @return Date created as a string
     */
    public String getDateCreated() {
        return dateCreated;
    }
}