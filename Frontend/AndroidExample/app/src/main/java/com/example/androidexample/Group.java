package com.example.androidexample;

/**
 * Represents a group with an ID, name, and capacity.
 * <p>
 * This class is typically used to model group data retrieved from a server.
 * </p>
 */
public class Group {

    /** The unique identifier of the group. */
    private int id;

    /** The name of the group. */
    private String group_name;

    /** The maximum capacity of the group. */
    private int capacity;

    /**
     * Returns the unique identifier of the group.
     *
     * @return the group ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the group.
     *
     * @return the group name
     */
    public String getGroupName() {
        return group_name;
    }

    /**
     * Returns the maximum capacity of the group.
     *
     * @return the group's capacity
     */
    public int getCapacity() {
        return capacity;
    }
}