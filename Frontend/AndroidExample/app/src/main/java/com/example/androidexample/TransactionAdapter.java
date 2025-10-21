package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    // Interface for handling clicks
    public interface OnTransactionClickListener {
        void onTransactionClick(JSONObject transaction);
    }

    private final List<JSONObject> transactionList;
    private final OnTransactionClickListener listener;

    public TransactionAdapter(List<JSONObject> transactionList, OnTransactionClickListener listener) {
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        JSONObject transaction = transactionList.get(position);
        try {
            holder.transactionNameText.setText(transaction.getString("name"));
            holder.transactionAmountText.setText("$" + transaction.getDouble("amount"));

            // Optional date field — safely handle if missing
            if (transaction.has("date")) {
                holder.transactionDateText.setText(transaction.getString("date"));
            } else {
                holder.transactionDateText.setText("No date");
            }

            holder.itemView.setOnClickListener(v -> listener.onTransactionClick(transaction));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionNameText, transactionAmountText, transactionDateText;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionNameText = itemView.findViewById(R.id.transactionNameText);
            transactionAmountText = itemView.findViewById(R.id.transactionAmountText);
            transactionDateText = itemView.findViewById(R.id.transactionDateText);
        }
    }
}