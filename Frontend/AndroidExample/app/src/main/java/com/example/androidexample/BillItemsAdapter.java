package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * BillItemsAdapter
 * ----------------
 * Simple RecyclerView adapter for displaying bill items.
 * Uses the row_bill_item.xml layout.
 */
public class BillItemsAdapter extends RecyclerView.Adapter<BillItemsAdapter.Holder> {

    private List<BillItem> items = new ArrayList<>();

    public BillItemsAdapter() {
    }

    /** Update adapter list */
    public void setItems(List<BillItem> newItems) {
        if (newItems == null) newItems = new ArrayList<>();
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_bill_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        BillItem item = items.get(position);

        holder.title.setText(item.itemName);

        double total = item.price * item.quantity;
        holder.amount.setText(String.format("$%.2f", total));

        holder.date.setText(item.paid ? "Paid" : "Unpaid");
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    /** ViewHolder for each bill item card */
    static class Holder extends RecyclerView.ViewHolder {

        TextView title, amount, date;

        public Holder(@NonNull View itemView) {
            super(itemView);

            title  = itemView.findViewById(R.id.billItemTitle);
            amount = itemView.findViewById(R.id.billItemAmount);
            date   = itemView.findViewById(R.id.billItemDate);
        }
    }
}
