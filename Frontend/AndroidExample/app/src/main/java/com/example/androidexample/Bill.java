package com.example.androidexample;

import com.google.gson.annotations.SerializedName;

public class Bill {

    @SerializedName("bill_id")
    private int billId;

    @SerializedName("bill_name")
    private String billName;

    @SerializedName("bill_amount")
    private String billAmount; // use String since backend sends it as string

    @SerializedName("dueTime")
    private String dueTime;

    @SerializedName("dueCreated")
    private String dueCreated;

    @SerializedName("paid")
    private boolean paid;

    // Getters
    public int getBillId() {
        return billId;
    }

    public String getBillName() {
        return billName;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public String getDueTime() {
        return dueTime;
    }

    public String getDueCreated() {
        return dueCreated;
    }

    public boolean isPaid() {
        return paid;
    }
}