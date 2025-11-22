package com.example.androidexample;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock storage class for managing a list of {@link Transaction} objects.
 * <p>
 * Provides methods to add transactions and retrieve transactions by group ID.
 * Useful for testing and development without a backend.
 */
public class TransactionMockStorage {

    /** Internal list storing all transactions. */
    private static final List<Transaction> transactions = new ArrayList<>();

    /**
     * Adds a new transaction to the mock storage.
     * <p>
     * Automatically assigns a unique ID based on the current list size.
     *
     * @param name    Name or description of the transaction.
     * @param amount  Monetary amount of the transaction.
     * @param groupId ID of the group associated with this transaction.
     * @param billId  ID of the bill associated with this transaction.
     * @param date    Date of the transaction.
     */
    public static void addTransaction(String name, double amount, int groupId, int billId, String date) {
        transactions.add(new Transaction(transactions.size() + 1, name, amount, groupId, billId, date));
    }

    /**
     * Retrieves all transactions associated with a specific group.
     *
     * @param groupId The ID of the group whose transactions are requested.
     * @return A list of transactions belonging to the specified group.
     */
    public static List<Transaction> getTransactionsForGroup(int groupId) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getGroupId() == groupId) list.add(t);
        }
        return list;
    }
}
