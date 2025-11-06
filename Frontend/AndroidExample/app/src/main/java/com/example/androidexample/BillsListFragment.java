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

        adapter = new BillsAdapter(billList, this::openBillDetail);
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            groupId = getArguments().getInt("groupId");
        }

        loadBills();

        return view;
    }

    private void loadBills() {
        // For now, fetch a single bill (bill 1) from local backend
        BillService billService = RetrofitClient.getLocalClient().create(BillService.class);
        Call<Bill> call = billService.getBillById(1);

        call.enqueue(new Callback<Bill>() {
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                if (response.isSuccessful() && response.body() != null) {
                    billList.clear();
                    billList.add(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                t.printStackTrace();
            }
        });
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