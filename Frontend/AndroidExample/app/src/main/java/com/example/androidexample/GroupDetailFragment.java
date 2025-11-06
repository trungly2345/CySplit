package com.example.androidexample;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupDetailFragment extends Fragment {

    private int groupId;
    private TextView groupNameTextView, groupDescriptionTextView;
    private CardView recentBillContainer;
    private TextView recentBillTitle, recentBillAmount;
    private Button buttonViewAllBills, buttonViewTransactions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);

        groupId = getArguments() != null ? getArguments().getInt("groupId") : -1;

        groupNameTextView = view.findViewById(R.id.groupNameTextView);
        groupDescriptionTextView = view.findViewById(R.id.groupDescriptionTextView);
        recentBillContainer = view.findViewById(R.id.recentBillContainer);
        recentBillTitle = view.findViewById(R.id.recentBillTitle);
        recentBillAmount = view.findViewById(R.id.recentBillAmount);
        buttonViewAllBills = view.findViewById(R.id.buttonViewAllBills);
        buttonViewTransactions = view.findViewById(R.id.buttonViewTransactions);

        // Load group info
        loadGroupInfo();

        // Load most recent bill
        loadRecentBill();

        // Click listeners
        recentBillContainer.setOnClickListener(v -> openBillDetail(recentBillId));

        buttonViewAllBills.setOnClickListener(v -> openAllBills());
        buttonViewTransactions.setOnClickListener(v -> openTransactions());

        return view;
    }

    private int recentBillId;

    private void loadGroupInfo() {
        // TODO: fetch from backend, for now mock
        groupNameTextView.setText("Awesome Group " + groupId);
        groupDescriptionTextView.setText("This is some info about this group.");
    }

    private void loadRecentBill() {
        // TODO: fetch most recent bill via HTTP, mock for now
        recentBillId = 1; // example
        recentBillTitle.setText("Dinner at Joe's");
        recentBillAmount.setText("$45.50");
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