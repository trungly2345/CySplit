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

import java.util.List;

public class TransactionFragment extends Fragment {

    private int groupId;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        recyclerView = view.findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            groupId = getArguments().getInt("groupId");
        }

        List<Transaction> transactions = TransactionMockStorage.getTransactionsForGroup(groupId);

        adapter = new TransactionAdapter(transactions, this::openTransactionOptions);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void openTransactionOptions(int transactionId) {
        TransactionOptionsFragment fragment = new TransactionOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("transactionId", transactionId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}