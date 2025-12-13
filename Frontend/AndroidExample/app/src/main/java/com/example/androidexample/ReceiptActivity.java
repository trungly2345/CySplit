package com.example.androidexample;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ReceiptActivity extends AppCompatActivity {

    private TextView totalText, paidText, unpaidText;
    private BillItemsStore store;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        store = BillItemsStore.getInstance();

        totalText = findViewById(R.id.receipt_total);
        paidText = findViewById(R.id.receipt_paid_total);
        unpaidText = findViewById(R.id.receipt_unpaid_total);

        calculateTotals();
    }

    private void calculateTotals() {
        List<BillItem> all = store.getAll();

        double total = 0;
        double paid = 0;
        double unpaid = 0;

        for (BillItem item : all) {
            total += item.amount;

            if (item.paid) {
                paid += item.amount;
            } else {
                unpaid += item.amount;
            }
        }

        totalText.setText(String.format("$%.2f", total));
        paidText.setText(String.format("$%.2f", paid));
        unpaidText.setText(String.format("$%.2f", unpaid));
    }
}
