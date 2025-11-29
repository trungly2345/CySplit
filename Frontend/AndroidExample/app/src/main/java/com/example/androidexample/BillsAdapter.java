package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of bills.
 * <p>
 * Each item shows the bill name and amount, and supports click handling via
 * {@link OnBillClickListener}.
 * </p>
 */
public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillViewHolder> {

    /** List of bills to display. */
    private List<Bill> bills;

    /** Listener for handling bill click events. */
    private OnBillClickListener listener;

    /**
     * Interface for handling clicks on individual bills.
     */
    public interface OnBillClickListener {
        /**
         * Called when a bill is clicked.
         *
         * @param billId The ID of the clicked bill.
         */
        void onBillClick(int billId);
    }

    /**
     * Constructs a new BillsAdapter.
     *
     * @param bills    List of bills to display
     * @param listener Listener for click events
     */
    public BillsAdapter(List<Bill> bills, OnBillClickListener listener) {
        this.bills = bills;
        this.listener = listener;
    }

    /**
     * Inflates the item view and returns a new ViewHolder.
     *
     * @param parent   Parent ViewGroup
     * @param viewType View type
     * @return New BillViewHolder
     */
    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    /**
     * Binds a bill to the ViewHolder and sets up the click listener.
     *
     * @param holder   The ViewHolder to bind
     * @param position Position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.title.setText(bill.getBillName());
        holder.amount.setText("$" + bill.getBillAmount());

        holder.itemView.setOnClickListener(v -> listener.onBillClick(bill.getBillId()));
    }

    /**
     * Returns the total number of bills in the list.
     *
     * @return Number of bills
     */
    @Override
    public int getItemCount() {
        return bills.size();
    }

    /**
     * ViewHolder class for displaying individual bill items.
     */
    static class BillViewHolder extends RecyclerView.ViewHolder {
        /** TextView for displaying the bill name. */
        TextView title;

        /** TextView for displaying the bill amount. */
        TextView amount;

        /**
         * Constructs a new BillViewHolder.
         *
         * @param itemView The item view
         */
        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.billItemTitle);
            amount = itemView.findViewById(R.id.billItemAmount);
        }
    }
}