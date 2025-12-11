package com.example.androidexample;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity {

    private TextView titleTv, totalTv, subtotalTv, taxTv, tipTv;
    private LinearLayout itemsContainer;

    private int groupId;
    private ArrayList<BillItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        titleTv = findViewById(R.id.restaurant_name);
        totalTv = findViewById(R.id.total_amount);
        subtotalTv = findViewById(R.id.receipt_subtotal);
        taxTv = findViewById(R.id.receipt_tax);
        tipTv = findViewById(R.id.receipt_tip);
        itemsContainer = findViewById(R.id.items_list_container);

        groupId = getIntent().getIntExtra("groupId", -1);

        loadDummyReceipt();
        render();
    }

    private void loadDummyReceipt() {
        items.clear();
        items.add(new BillItem(1, "Pizza Margherita", 15.99, 2, false));
        items.add(new BillItem(2, "Caesar Salad", 8.50, 1, true));
        items.add(new BillItem(3, "Soda", 2.50, 1, false));
    }

    private void render() {
        titleTv.setText("Table Receipt");
        double subtotal = 0;
        for (BillItem b : items) subtotal += b.price * b.quantity;
        double tax = subtotal * 0.08;
        double tip = subtotal * 0.15;
        double total = subtotal + tax + tip;

        subtotalTv.setText(String.format("Subtotal: $%.2f", subtotal));
        taxTv.setText(String.format("Tax: $%.2f", tax));
        tipTv.setText(String.format("Tip: $%.2f", tip));
        totalTv.setText(String.format("Total: $%.2f", total));

        itemsContainer.removeAllViews();
        for (BillItem b : items) {
            View v = getLayoutInflater().inflate(R.layout.item_bill_share_simple, itemsContainer, false);
            TextView name = v.findViewById(R.id.share_username);
            TextView amount = v.findViewById(R.id.share_amount);
            TextView paid = v.findViewById(R.id.share_paid_status);
            name.setText(b.itemName + " x" + b.quantity);
            amount.setText(String.format("$%.2f", b.price * b.quantity));
            paid.setText(b.paid ? "PAID" : "UNPAID");
            itemsContainer.addView(v);
        }
    }
}
