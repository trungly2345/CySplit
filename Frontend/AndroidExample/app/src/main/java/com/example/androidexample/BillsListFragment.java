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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillsListFragment extends Fragment {

    private int groupId;
    private RecyclerView recyclerView;
    private BillsAdapter adapter;
    private List<Bill> billList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills_list, container, false);

        recyclerView = view.findViewById(R.id.billsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            groupId = getArguments().getInt("groupId");
        }

        adapter = new BillsAdapter(billList, billId -> openBillDetail(billId, groupId));
        recyclerView.setAdapter(adapter);

        loadBills();

        return view;
    }

    private void loadBills() {
        BillService billService = RetrofitClient.getLocalClient().create(BillService.class);
        Call<List<Bill>> call = billService.getAllBills();

        call.enqueue(new Callback<List<Bill>>() {
            @Override
            public void onResponse(Call<List<Bill>> call, Response<List<Bill>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    billList.clear();
                    billList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    // Optional: handle API errors or empty list
                    System.out.println("Failed to load bills: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Bill>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void openBillDetail(int billId, int groupId) {
        BillDetailFragment fragment = new BillDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("billId", billId);
        bundle.putInt("groupId", groupId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}