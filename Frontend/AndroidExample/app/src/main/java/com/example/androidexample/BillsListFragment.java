package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BillsListFragment extends Fragment {

    private int groupId;
    private RecyclerView recyclerView;
    private BillsAdapter adapter;
    private List<JSONObject> billList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills_list, container, false);

        recyclerView = view.findViewById(R.id.billsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        billList = new ArrayList<>();
        adapter = new BillsAdapter(billList, this::openBillDetail);
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            groupId = getArguments().getInt("groupId");
            loadBills();
        }

        return view;
    }

    private void loadBills() {
        // TODO: Replace with real backend API call
        try {
            for (int i = 1; i <= 10; i++) {
                JSONObject bill = new JSONObject();
                bill.put("id", i);
                bill.put("title", "Bill " + i);
                bill.put("amount", i * 10.0);
                bill.put("date", "Nov " + i);
                billList.add(bill);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openBillDetail(int billId) {
        BillDetailFragment fragment = new BillDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("billId", billId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}