package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillViewHolder> {
    private final List<JSONObject> bills;
    private final OnBillClickListener listener;

    public interface OnBillClickListener {
        void onBillClick(int billId);
    }

    public BillsAdapter(List<JSONObject> bills, OnBillClickListener listener) {
        this.bills = bills;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        JSONObject bill = bills.get(position);
        try {
            holder.title.setText(bill.getString("title"));
            holder.amount.setText("$" + bill.getDouble("amount"));
            holder.date.setText(bill.getString("date"));

            int billId = bill.getInt("id");
            holder.itemView.setOnClickListener(v -> listener.onBillClick(billId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, date;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.billItemTitle);
            amount = itemView.findViewById(R.id.billItemAmount);
            date = itemView.findViewById(R.id.billItemDate);
        }
    }
}