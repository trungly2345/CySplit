package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment that displays options for a specific {@link Transaction}.
 * <p>
 * Provides buttons to view the chat related to the transaction or to view the associated bill.
 * Handles navigation to {@link ChatFragment} and {@link BillFragment}.
 */
public class TransactionOptionsFragment extends Fragment {

    /** ID of the transaction for which options are displayed. */
    private int transactionId;

    /**
     * Inflates the fragment layout and initializes option buttons.
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

    /**
     * Opens the {@link ChatFragment} for the current transaction.
     */
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

    /**
     * Opens the {@link BillFragment} for the current transaction.
     */
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