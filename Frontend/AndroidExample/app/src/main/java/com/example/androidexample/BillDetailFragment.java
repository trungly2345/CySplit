package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

public class BillDetailFragment extends Fragment {

    private int billId;
    private int groupId; // optional, for back navigation
    private TextView billTitleTextView, billAmountTextView, billDescriptionTextView;
    private Button payButton, backToBillsButton;

    private boolean isPaid = false; // mock status

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_detail, container, false);

        billTitleTextView = view.findViewById(R.id.billTitleTextView);
        billAmountTextView = view.findViewById(R.id.billAmountTextView);
        billDescriptionTextView = view.findViewById(R.id.billDescriptionTextView);
        payButton = view.findViewById(R.id.payButton);
        backToBillsButton = view.findViewById(R.id.backToBillsButton);

        if (getArguments() != null) {
            billId = getArguments().getInt("billId");
            groupId = getArguments().getInt("groupId", -1); // optional
        }

        loadBillInfo();
        setupButtons();
        loadChat();

        return view;
    }

    private void loadBillInfo() {
        // TODO: Replace mock data with backend API call
        billTitleTextView.setText("Dinner at Joe's");
        billAmountTextView.setText("$45.50");
        billDescriptionTextView.setText("Split among 4 people");

        isPaid = false; // mock unpaid
        payButton.setEnabled(!isPaid);
        payButton.setText(isPaid ? "Paid" : "Pay");
    }

    private void setupButtons() {
        payButton.setOnClickListener(v -> payBill());
        backToBillsButton.setOnClickListener(v -> openAllBills());
    }

    private void payBill() {
        // TODO: Call backend API to mark bill as paid
        isPaid = true;
        payButton.setEnabled(false);
        payButton.setText("Paid");
    }

    private void openAllBills() {
        if (groupId == -1) return; // safety check

        BillsListFragment fragment = new BillsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("groupId", groupId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadChat() {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("transactionId", billId); // link chat to this bill
        chatFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.billChatContainer, chatFragment)
                .commit();
    }
}