package com.example.androidexample;

/**
 * Represents a financial transaction within the application.
 * <p>
 * Each transaction has an ID, a name/description, an amount, associated group and bill IDs,
 * and a date indicating when the transaction occurred.
 */
public class Transaction {

    /** Unique identifier for the transaction. */
    private int id;

    /** Name or description of the transaction. */
    private String name;

    /** Monetary amount of the transaction. */
    private double amount;

    /** Identifier of the group associated with this transaction. */
    private int groupId;

    /** Identifier of the bill associated with this transaction. */
    private int billId;

    /** Date of the transaction in string format. */
    private String date;

    /**
     * Constructs a new Transaction with the specified details.
     *
     * @param id      Unique ID of the transaction.
     * @param name    Name or description of the transaction.
     * @param amount  Monetary amount of the transaction.
     * @param groupId ID of the associated group.
     * @param billId  ID of the associated bill.
     * @param date    Date of the transaction.
     */
    public Transaction(int id, String name, double amount, int groupId, int billId, String date) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.groupId = groupId;
        this.billId = billId;
        this.date = date;
    }

    /** @return The unique ID of the transaction. */
    public int getId() { return id; }

    /** @return The name or description of the transaction. */
    public String getName() { return name; }

    /** @return The monetary amount of the transaction. */
    public double getAmount() { return amount; }

    /** @return The group ID associated with the transaction. */
    public int getGroupId() { return groupId; }

    /** @return The bill ID associated with the transaction. */
    public int getBillId() { return billId; }

    /** @return The date of the transaction. */
    public String getDate() { return date; }
}