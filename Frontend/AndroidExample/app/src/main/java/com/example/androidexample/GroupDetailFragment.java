package com.example.androidexample;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupDetailFragment extends Fragment {

    private int groupId;
    private TextView groupNameTextView, groupDescriptionTextView;
    private CardView recentBillContainer;
    private TextView recentBillTitle, recentBillAmount;
    private Button buttonViewAllBills, buttonViewTransactions, backButton;

    private int recentBillId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);

        // UI references
        groupNameTextView = view.findViewById(R.id.groupNameTextView);
        groupDescriptionTextView = view.findViewById(R.id.groupDescriptionTextView);
        recentBillContainer = view.findViewById(R.id.recentBillContainer);
        recentBillTitle = view.findViewById(R.id.recentBillTitle);
        recentBillAmount = view.findViewById(R.id.recentBillAmount);
        buttonViewAllBills = view.findViewById(R.id.buttonViewAllBills);
        buttonViewTransactions = view.findViewById(R.id.buttonViewTransactions);

        // Get groupId from arguments
        groupId = getArguments() != null ? getArguments().getInt("groupId") : -1;

        // Load group info from live server
        loadGroupInfo();

        // Load most recent bill from local server
        loadRecentBill();

        // Click listeners
        recentBillContainer.setOnClickListener(v -> openBillDetail(recentBillId));
        buttonViewAllBills.setOnClickListener(v -> openAllBills());
        buttonViewTransactions.setOnClickListener(v -> openTransactions());

        return view;
    }

    private void loadGroupInfo() {
        GroupService groupService = RetrofitClient.getRemoteClient().create(GroupService.class);
        Call<Group> call = groupService.getGroupById(groupId);

        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Group group = response.body();
                    groupNameTextView.setText(group.getGroupName());
                    groupDescriptionTextView.setText("Capacity: " + group.getCapacity());
                } else {
                    groupNameTextView.setText("Group " + groupId);
                    groupDescriptionTextView.setText("No info available");
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.e("GroupDetailFragment", "Failed to load group", t);
                t.printStackTrace();
                groupNameTextView.setText("Group " + groupId);
                groupDescriptionTextView.setText("No info available");
            }
        });
    }

    private void loadRecentBill() {
        BillService billService = RetrofitClient.getLocalClient().create(BillService.class);
        Call<Bill> call = billService.getBillById(1); // for now, always bill 1

        call.enqueue(new Callback<Bill>() {
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bill bill = response.body();
                    recentBillId = bill.getBillId();
                    recentBillTitle.setText(bill.getBillName());
                    recentBillAmount.setText("$" + bill.getBillAmount());
                } else {
                    setRecentBillFallback();
                }
            }

            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                Log.e("GroupDetailFragment", "Failed to load bill", t);
                t.printStackTrace();
                setRecentBillFallback();
            }
        });
    }

    private void setRecentBillFallback() {
        recentBillId = 1;
        recentBillTitle.setText("Bill 1");
        recentBillAmount.setText("$0.00");
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

    private void openAllBills() {
        BillsListFragment fragment = new BillsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("groupId", groupId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openTransactions() {
        TransactionFragment fragment = new TransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("groupId", groupId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}