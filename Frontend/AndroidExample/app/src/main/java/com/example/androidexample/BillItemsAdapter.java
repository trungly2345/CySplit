package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class BillItemsAdapter extends RecyclerView.Adapter<BillItemsAdapter.Holder> {

    private ArrayList<BillItem> items;
    private final BiConsumer<BillItem, BillItemsActivity.Action> callback;

    public BillItemsAdapter(ArrayList<BillItem> items,
                            BiConsumer<BillItem, BillItemsActivity.Action> callback) {
        this.items = new ArrayList<>(items);
        this.callback = callback;
    }

    public void updateList(ArrayList<BillItem> newItems) {
        this.items = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_bill_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder h, int pos) {
        BillItem b = items.get(pos);

        h.name.setText(b.itemName);
        h.qty.setText("x" + b.quantity);
        h.price.setText("$" + String.format("%.2f", b.price));
        h.paid.setText(b.paid ? "Paid" : "Unpaid");
        h.paid.setTextColor(b.paid ? 0xFF006600 : 0xFFAA0000);

        h.btnToggle.setOnClickListener(v -> callback.accept(b, BillItemsActivity.Action.TOGGLE_PAID));
        h.btnEdit.setOnClickListener(v -> callback.accept(b, BillItemsActivity.Action.EDIT));
        h.btnDelete.setOnClickListener(v -> callback.accept(b, BillItemsActivity.Action.DELETE));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView name, qty, price, paid;
        View btnToggle, btnEdit, btnDelete;

        public Holder(View v) {
            super(v);
            name = v.findViewById(R.id.item_name);
            qty = v.findViewById(R.id.item_qty);
            price = v.findViewById(R.id.item_price);
            paid = v.findViewById(R.id.item_paid_state);

            btnToggle = v.findViewById(R.id.btn_toggle_paid);
            btnEdit = v.findViewById(R.id.btn_edit_item);
            btnDelete = v.findViewById(R.id.btn_delete_item);
        }
    }
}
