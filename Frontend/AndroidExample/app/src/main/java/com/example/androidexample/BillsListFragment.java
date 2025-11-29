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

/**
 * Fragment for displaying a list of bills associated with a specific group.
 * <p>
 * This fragment uses a RecyclerView with a {@link BillsAdapter} to display
 * all bills retrieved from the backend API. Clicking a bill opens the detailed
 * bill view in {@link BillDetailFragment}.
 * </p>
 */
public class BillsListFragment extends Fragment {

    /** The ID of the group whose bills are displayed. */
    private int groupId;

    /** RecyclerView for displaying bills. */
    private RecyclerView recyclerView;

    /** Adapter for binding bills to the RecyclerView. */
    private BillsAdapter adapter;

    /** List of bills to display. */
    private List<Bill> billList = new ArrayList<>();

    /**
     * Called to create the fragment's view hierarchy.
     *
     * @param inflater           LayoutInflater to inflate views
     * @param container          Parent container
     * @param savedInstanceState Saved state bundle
     * @return The root view of the fragment
     */
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

    /**
     * Loads all bills from the backend API and updates the RecyclerView.
     */
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
                    System.out.println("Failed to load bills: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Bill>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Opens the detailed view for a specific bill.
     *
     * @param billId  The ID of the bill to open
     * @param groupId The ID of the group the bill belongs to
     */
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