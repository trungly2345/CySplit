package com.example.androidexample;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BillItemsActivity extends AppCompatActivity {

    private int groupId;
    private String groupName;

    private TabHost tabHost;
    private RecyclerView recycler;
    private BillItemsAdapter adapter;
    private Button btnAdd, btnMarkAll;

    private ArrayList<BillItem> allItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_items);

        groupId = getIntent().getIntExtra("groupId", -1);
        groupName = getIntent().getStringExtra("groupName");

        tabHost = findViewById(R.id.bill_tabhost);
        recycler = findViewById(R.id.bill_items_recycler);
        btnAdd = findViewById(R.id.btn_add_item);
        btnMarkAll = findViewById(R.id.btn_mark_all);

        setupTabs();
        setupRecycler();

        loadDummyItems();

        btnAdd.setOnClickListener(v -> showAddItemDialog());
        btnMarkAll.setOnClickListener(v -> markAllUnpaidPaid());
    }

    private void setupTabs() {
        tabHost.setup();

        TabHost.TabSpec t1 = tabHost.newTabSpec("all");
        t1.setIndicator("All");
        t1.setContent(R.id.tab_all);
        tabHost.addTab(t1);

        TabHost.TabSpec t2 = tabHost.newTabSpec("unpaid");
        t2.setIndicator("Unpaid");
        t2.setContent(R.id.tab_all);
        tabHost.addTab(t2);

        TabHost.TabSpec t3 = tabHost.newTabSpec("paid");
        t3.setIndicator("Paid");
        t3.setContent(R.id.tab_all);
        tabHost.addTab(t3);

        tabHost.setOnTabChangedListener(tabId -> {
            switch (tabId) {
                case "all": adapter.updateList(allItems); break;
                case "unpaid": filterUnpaid(); break;
                case "paid": filterPaid(); break;
            }
        });
    }

    private void setupRecycler() {
        adapter = new BillItemsAdapter(allItems, this::onItemAction);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    private void loadDummyItems() {
        allItems.clear();
        allItems.add(new BillItem(1, "Pizza Margherita", 15.99, 2, false));
        allItems.add(new BillItem(2, "Caesar Salad", 8.50, 1, true));
        allItems.add(new BillItem(3, "Chicken Wings", 12.00, 1, false));
        adapter.updateList(allItems);
    }

    private void filterUnpaid() {
        ArrayList<BillItem> out = new ArrayList<>();
        for (BillItem b : allItems) if (!b.paid) out.add(b);
        adapter.updateList(out);
    }

    private void filterPaid() {
        ArrayList<BillItem> out = new ArrayList<>();
        for (BillItem b : allItems) if (b.paid) out.add(b);
        adapter.updateList(out);
    }

    private void showAddItemDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null);
        EditText nameEt = v.findViewById(R.id.add_item_name);
        EditText priceEt = v.findViewById(R.id.add_item_price);
        priceEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(this)
                .setTitle("Add Item (UI-only)")
                .setView(v)
                .setPositiveButton("Add", (d, w) -> {
                    String name = nameEt.getText().toString().trim();
                    String priceS = priceEt.getText().toString().trim();
                    double price = priceS.isEmpty() ? 0.0 : Double.parseDouble(priceS);
                    int nextId = allItems.size() + 1;
                    BillItem item = new BillItem(nextId, name, price, 1, false);
                    allItems.add(item);
                    adapter.updateList(allItems);
                    tabHost.setCurrentTabByTag("all");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void markAllUnpaidPaid() {
        for (BillItem b : allItems) if (!b.paid) b.paid = true;
        adapter.updateList(allItems);
        tabHost.setCurrentTabByTag("paid");
    }

    private void onItemAction(BillItem item, Action action) {
        switch (action) {
            case TOGGLE_PAID:
                item.paid = !item.paid;
                adapter.notifyDataSetChanged();
                break;
            case DELETE:
                allItems.remove(item);
                adapter.updateList(allItems);
                break;
            case EDIT:
                View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null);
                EditText nameEt = v.findViewById(R.id.add_item_name);
                EditText priceEt = v.findViewById(R.id.add_item_price);
                nameEt.setText(item.itemName);
                priceEt.setText(String.valueOf(item.price));
                new AlertDialog.Builder(this)
                        .setTitle("Edit Item (UI-only)")
                        .setView(v)
                        .setPositiveButton("Save", (d, w) -> {
                            item.itemName = nameEt.getText().toString().trim();
                            try { item.price = Double.parseDouble(priceEt.getText().toString().trim()); } catch (Exception e){ }
                            adapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                break;
        }
    }

    public enum Action { TOGGLE_PAID, DELETE, EDIT }
}
