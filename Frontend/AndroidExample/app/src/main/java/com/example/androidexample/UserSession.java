package com.example.androidexample;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton class that manages the current user's session.
 * <p>
 * Provides methods to store and retrieve the username in memory and optionally load it
 * from {@link SharedPreferences}.
 */
public class UserSession {

    /** Singleton instance of UserSession. */
    private static UserSession instance;

    /** The username of the currently logged-in user. */
    private String username;

    /** Private constructor to enforce singleton pattern. */
    private UserSession() {}

    /**
     * Returns the singleton instance of {@link UserSession}.
     *
     * @return The UserSession instance.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Sets the username for the current session.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the current session.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Loads the username from {@link SharedPreferences}.
     * <p>
     * If no username is stored, defaults to "Unknown".
     *
     * @param context The application context.
     */
    public void loadFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.username = prefs.getString("email", "Unknown");
    }
}
