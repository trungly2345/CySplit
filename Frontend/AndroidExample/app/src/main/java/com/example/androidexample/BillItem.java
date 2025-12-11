package com.example.androidexample;

public class BillItem {
    public int id;
    public String itemName;
    public double price;
    public int quantity;
    public boolean paid;

    public BillItem(int id, String itemName, double price, int quantity, boolean paid) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.paid = paid;
    }
}
