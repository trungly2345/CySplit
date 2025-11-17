package com.example.androidexample;

import java.util.ArrayList;
import java.util.List;

public class TransactionMockStorage {

    private static final List<Transaction> transactions = new ArrayList<>();

    public static void addTransaction(String name, double amount, int groupId, int billId, String date) {
        transactions.add(new Transaction(transactions.size() + 1, name, amount, groupId, billId, date));
    }

    public static List<Transaction> getTransactionsForGroup(int groupId) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getGroupId() == groupId) list.add(t);
        }
        return list;
    }
}