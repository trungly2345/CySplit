package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TransactionOptionsFragment extends Fragment {

    private int transactionId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_options, container, false);

        if (getArguments() != null) {
            transactionId = getArguments().getInt("transactionId");
        }

        Button chatButton = view.findViewById(R.id.buttonViewChat);
        Button billButton = view.findViewById(R.id.buttonViewBill);

        chatButton.setOnClickListener(v -> openChat());
        billButton.setOnClickListener(v -> openBill());

        return view;
    }

    private void openChat() {
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("transactionId", transactionId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openBill() {
        BillFragment fragment = new BillFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("transactionId", transactionId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}