package com.example.androidexample;

import android.util.Log;
import androidx.annotation.MainThread;

import java.util.*;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class BillItemsStore {

    private static BillItemsStore instance;

    private final BillItemsApi api;
    private final ArrayList<BillItem> allItems = new ArrayList<>();
    private final ArrayList<Listener> listeners = new ArrayList<>();

    private int groupId = -1;

    public static BillItemsStore getInstance() {
        if (instance == null) instance = new BillItemsStore();
        return instance;
    }

    private BillItemsStore() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://coms-3090-039.class.las.iastate.edu:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(BillItemsApi.class);
    }

    public void setGroupId(int id) {
        this.groupId = id;
    }

    // ======== Public Getters ========
    public List<BillItem> getAll() { return new ArrayList<>(allItems); }
    public List<BillItem> getPaid() {
        ArrayList<BillItem> list = new ArrayList<>();
        for (BillItem b: allItems) if (b.paid) list.add(b);
        return list;
    }
    public List<BillItem> getUnpaid() {
        ArrayList<BillItem> list = new ArrayList<>();
        for (BillItem b: allItems) if (!b.paid) list.add(b);
        return list;
    }

    // ======== Listener Support ========
    public interface Listener {
        void onItemsUpdated();
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    @MainThread
    private void notifyListeners() {
        for (Listener l : listeners) l.onItemsUpdated();
    }

    // ======== Backend Fetch Methods ========
    public void refreshAll() {
        api.getAll(groupId).enqueue(new Callback<List<BillItemDto>>() {
            @Override
            public void onResponse(Call<List<BillItemDto>> call,
                                   Response<List<BillItemDto>> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                allItems.clear();

                for (BillItemDto dto : response.body()) {
                    BillItem item = new BillItem(
                            dto.itemId,
                            dto.itemName,
                            dto.quantity,
                            Double.parseDouble(dto.itemPrice),
                            dto.paid
                    );
                    item.amount = item.price * item.quantity;
                    allItems.add(item);
                }

                notifyListeners();
            }

            @Override
            public void onFailure(Call<List<BillItemDto>> call, Throwable t) {
                Log.e("BillStore", "Load error", t);
            }
        });

    }

    public void refreshPaid() { refreshAll(); }
    public void refreshUnpaid() { refreshAll(); }

    // ======== CRUD ========
    public void addItem(BillItem newItem) {
        api.createItem(groupId, newItem).enqueue(new Callback<BillItem>() {
            @Override
            public void onResponse(Call<BillItem> call, Response<BillItem> response) {
                refreshAll();
            }
            @Override
            public void onFailure(Call<BillItem> call, Throwable t) {}
        });
    }

    public void togglePaid(int itemId) {
        api.markPaid(itemId).enqueue(new Callback<BillItem>() {
            @Override public void onResponse(Call<BillItem> call, Response<BillItem> response) { refreshAll(); }
            @Override public void onFailure(Call<BillItem> call, Throwable t) {}
        });
    }

    public void deleteItem(int itemId) {
        api.deleteItem(itemId).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) { refreshAll(); }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}
