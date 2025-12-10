package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying a list of {@link Transaction} objects in a RecyclerView.
 * <p>
 * Each item shows the transaction's name, amount, and date. Clicking an item
 * triggers a callback via {@link OnTransactionClickListener}.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    /** List of transactions to display. */
    private List<Transaction> transactions;

    /** Listener to handle transaction click events. */
    private OnTransactionClickListener listener;

    /**
     * Interface for handling clicks on individual transactions.
     */
    public interface OnTransactionClickListener {
        /**
         * Called when a transaction item is clicked.
         *
         * @param transactionId The ID of the clicked transaction.
         */
        void onTransactionClick(int transactionId);
    }

    /**
     * Constructs a new TransactionAdapter.
     *
     * @param transactions List of transactions to display.
     * @param listener     Listener for handling transaction clicks.
     */
    public TransactionAdapter(List<Transaction> transactions, OnTransactionClickListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    /**
     * Creates a new ViewHolder for a transaction item.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type of the new view.
     * @return A new TransactionViewHolder.
     */
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    /**
     * Binds a transaction's data to the provided ViewHolder.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the transaction in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transactions.get(position);
        holder.name.setText(t.getName());
        holder.amount.setText("$" + t.getAmount());
        holder.date.setText(t.getDate());

        holder.itemView.setOnClickListener(v -> listener.onTransactionClick(t.getId()));
    }

    /**
     * Returns the total number of transactions in the list.
     *
     * @return Number of transactions.
     */
    @Override
    public int getItemCount() {
        return transactions.size();
    }

    /**
     * ViewHolder for a single transaction item.
     * <p>
     * Holds references to the TextViews for name, amount, and date.
     */
    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        /** TextView displaying the transaction's name. */
        TextView name;

        /** TextView displaying the transaction's amount. */
        TextView amount;

        /** TextView displaying the transaction's date. */
        TextView date;

        /**
         * Constructs a TransactionViewHolder and initializes the TextViews.
         *
         * @param itemView The root view of the transaction item layout.
         */
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.transactionNameText);
            amount = itemView.findViewById(R.id.transactionAmountText);
            date = itemView.findViewById(R.id.transactionDateText);
        }
    }
}
