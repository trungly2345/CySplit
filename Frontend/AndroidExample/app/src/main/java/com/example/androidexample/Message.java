package com.example.androidexample;

/**
 * Represents a single chat message in the application.
 * <p>
 * Contains details about the sender, message content, timestamp, date, and an associated profile image resource.
 * </p>
 */
public class Message {

    /** Username of the message sender. */
    private String username;

    /** Text content of the message. */
    private String text;

    /** Time the message was sent, formatted as a string (e.g., "12:34 PM"). */
    private String time;

    /** Date the message was sent, formatted as a string (e.g., "Nov 21"). */
    private String date;

    /** Resource ID for the sender's profile image. */
    private int profileRes;

    /**
     * Constructs a new Message object.
     *
     * @param username   the username of the sender
     * @param text       the message content
     * @param time       the time the message was sent
     * @param date       the date the message was sent
     * @param profileRes the resource ID for the sender's profile image
     */
    public Message(String username, String text, String time, String date, int profileRes) {
        this.username = username;
        this.text = text;
        this.time = time;
        this.date = date;
        this.profileRes = profileRes;
    }

    /** @return the sender's username */
    public String getUsername() { return username; }

    /** @return the message text */
    public String getText() { return text; }

    /** @return the time the message was sent */
    public String getTime() { return time; }

    /** @return the date the message was sent */
    public String getDate() { return date; }

    /** @return the profile image resource ID */
    public int getProfileRes() { return profileRes; }
}