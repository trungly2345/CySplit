package com.example.androidexample;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BillUnpaidFragment extends Fragment implements BillItemsStore.Listener {

    private BillItemsAdapter adapter;

    public BillUnpaidFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bill_list, container, false);

        RecyclerView recycler = v.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BillItemsAdapter();
        recycler.setAdapter(adapter);

        BillItemsStore.getInstance().addListener(this);

        loadData();

        return v;
    }

    private void loadData() {
        BillItemsStore.getInstance().refreshAll();
    }

    @Override
    public void onItemsUpdated() {
        adapter.setItems(BillItemsStore.getInstance().getUnpaid());
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BillItemsStore.getInstance().addListener(this);
    }
}
