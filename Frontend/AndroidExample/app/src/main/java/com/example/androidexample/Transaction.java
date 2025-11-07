package com.example.androidexample;

public class Transaction {

    private int id;
    private String name;
    private double amount;
    private int groupId;
    private int billId;
    private String date; // new field

    public Transaction(int id, String name, double amount, int groupId, int billId, String date) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.groupId = groupId;
        this.billId = billId;
        this.date = date;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public int getGroupId() { return groupId; }
    public int getBillId() { return billId; }
    public String getDate() { return date; }
}