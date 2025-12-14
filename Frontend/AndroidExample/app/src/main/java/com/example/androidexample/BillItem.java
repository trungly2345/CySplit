package com.example.androidexample;

public class BillItem {
    public int itemId;
    public String itemName;
    public int quantity;
    public double price;
    public boolean paid;
    public double amount;

    public BillItem(int itemId, String itemName, int quantity, double price, boolean paid) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.paid = paid;
    }

    
}
