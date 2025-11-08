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

    private List<Bill> bills;
    private OnBillClickListener listener;

    public interface OnBillClickListener {
        void onBillClick(int billId);
    }

    public BillsAdapter(List<Bill> bills, OnBillClickListener listener) {
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
        Bill bill = bills.get(position);
        holder.title.setText(bill.getBillName());
        holder.amount.setText("$" + bill.getBillAmount());

        holder.itemView.setOnClickListener(v -> listener.onBillClick(bill.getBillId()));
    }


    @Override
    public int getItemCount() {
        return bills.size();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.billItemTitle);
            amount = itemView.findViewById(R.id.billItemAmount);
        }
    }
}