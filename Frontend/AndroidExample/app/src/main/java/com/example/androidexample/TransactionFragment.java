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

/**
 * Fragment for displaying a list of {@link Transaction} objects associated with a specific group.
 * <p>
 * This fragment uses a {@link RecyclerView} with a {@link TransactionAdapter} to display transactions.
 * Clicking a transaction opens {@link TransactionOptionsFragment} for further actions.
 */
public class TransactionFragment extends Fragment {

    /** ID of the group whose transactions are displayed. */
    private int groupId;

    /** RecyclerView for displaying transactions. */
    private RecyclerView recyclerView;

    /** Adapter for binding transaction data to the RecyclerView. */
    private TransactionAdapter adapter;

    /**
     * Inflates the fragment's layout, initializes the RecyclerView and adapter,
     * and loads transactions for the specified group.
     *
     * @param inflater           LayoutInflater to inflate the fragment layout.
     * @param container          The parent ViewGroup that the fragment's UI should attach to.
     * @param savedInstanceState Bundle containing saved state of the fragment.
     * @return The root view of the fragment layout.
     */
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

    /**
     * Opens the {@link TransactionOptionsFragment} for a specific transaction.
     *
     * @param transactionId The ID of the transaction to manage.
     */
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