package com.example.androidexample;

/**
 * Represents a single notification item.
 * <p>
 * Each notification has a title, a message, a timestamp, and an expanded/collapsed state.
 * The expanded state controls whether the full message is visible in the UI.
 * </p>
 */
public class NotificationItem {

    /** The notification title. */
    private String title;

    /** The notification message. */
    private String message;

    /** The timestamp of the notification. */
    private String timestamp;

    /** Whether the notification is expanded to show the full message. */
    private boolean expanded = false;

    /**
     * Constructs a new NotificationItem.
     *
     * @param title     the notification title
     * @param message   the notification message
     * @param timestamp the timestamp of the notification
     */
    public NotificationItem(String title, String message, String timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    /** Returns the notification title. */
    public String getTitle() {
        return title;
    }

    /** Returns the notification message. */
    public String getMessage() {
        return message;
    }

    /** Returns the timestamp of the notification. */
    public String getTimestamp() {
        return timestamp;
    }

    /** Returns whether the notification is expanded. */
    public boolean isExpanded() {
        return expanded;
    }

    /** Sets the expanded state of the notification. */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}