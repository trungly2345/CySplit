package com.example.androidexample;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a bill with details such as ID, name, amount, due time, creation time, and payment status.
 * <p>
 * This class is used in conjunction with Gson to deserialize JSON responses from the backend.
 * Fields are annotated with {@link SerializedName} to map JSON keys to Java fields.
 * </p>
 */
public class Bill {

    /** The unique identifier for the bill. */
    @SerializedName("bill_id")
    private int billId;

    /** The name or title of the bill. */
    @SerializedName("bill_name")
    private String billName;

    /**
     * The amount of the bill.
     * <p>
     * Stored as a String because the backend sends it in string format.
     * </p>
     */
    @SerializedName("bill_amount")
    private String billAmount;

    /** The due date/time of the bill in string format. */
    @SerializedName("dueTime")
    private String dueTime;

    /** The creation date/time of the bill in string format. */
    @SerializedName("dueCreated")
    private String dueCreated;

    /** Indicates whether the bill has been paid. */
    @SerializedName("paid")
    private boolean paid;

    // Getters

    /**
     * Returns the unique identifier of the bill.
     *
     * @return the bill ID
     */
    public int getBillId() {
        return billId;
    }

    /**
     * Returns the name or title of the bill.
     *
     * @return the bill name
     */
    public String getBillName() {
        return billName;
    }

    /**
     * Returns the amount of the bill.
     *
     * @return the bill amount as a string
     */
    public String getBillAmount() {
        return billAmount;
    }

    /**
     * Returns the due date/time of the bill.
     *
     * @return the due time as a string
     */
    public String getDueTime() {
        return dueTime;
    }

    /**
     * Returns the creation date/time of the bill.
     *
     * @return the creation time as a string
     */
    public String getDueCreated() {
        return dueCreated;
    }

    /**
     * Returns whether the bill has been paid.
     *
     * @return {@code true} if the bill is paid, {@code false} otherwise
     */
    public boolean isPaid() {
        return paid;
    }
}