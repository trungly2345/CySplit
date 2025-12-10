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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment for displaying detailed information about a specific bill.
 * <p>
 * Shows bill name, amount, due date, and status (paid/unpaid). Allows users to mark
 * the bill as paid and automatically records a transaction. Integrates a chat fragment
 * for the group associated with the bill.
 * </p>
 */
public class BillDetailFragment extends Fragment {

    /** ID of the bill being displayed. */
    private int billId;

    /** ID of the group the bill belongs to. */
    private int groupId = 1; // default for mock

    /** Username of the current user. */
    private String userName = "";

    /** TextView displaying the bill's title/name. */
    private TextView billTitleTextView;

    /** TextView displaying the bill's amount. */
    private TextView billAmountTextView;

    /** TextView displaying the bill's description or due time. */
    private TextView billDescriptionTextView;

    /** TextView displaying the bill's paid/unpaid status. */
    private TextView billStatusTextView;

    /** Button allowing the user to mark the bill as paid. */
    private Button payButton;

    /** Button to navigate back to the group detail view. */
    private Button backButton;

    /**
     * Called to create the fragment's view.
     *
     * @param inflater           LayoutInflater to inflate views
     * @param container          Parent container view
     * @param savedInstanceState Saved state bundle
     * @return the root view of the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bill_detail, container, false);

        // Initialize UI elements
        billTitleTextView = view.findViewById(R.id.billTitleTextView);
        billAmountTextView = view.findViewById(R.id.billAmountTextView);
        billDescriptionTextView = view.findViewById(R.id.billDescriptionTextView);
        billStatusTextView = view.findViewById(R.id.billStatusTextView);
        payButton = view.findViewById(R.id.payButton);
        backButton = view.findViewById(R.id.backToGroupButton);

        // Get arguments from Bundle
        if (getArguments() != null) {
            billId = getArguments().getInt("billId");
            groupId = getArguments().getInt("groupId", 1);
            userName = getArguments().getString("userName", "Aaron");

            loadBillInfo();
        }

        // Setup click listeners
        payButton.setOnClickListener(v -> markBillAsPaid());
        backButton.setOnClickListener(v -> navigateBackToGroup());

        // Dynamically add ChatFragment if first creation
        if (savedInstanceState == null) {
            userName = UserSession.getInstance().getUsername();
            String groupName = "Group" + groupId;
            ChatFragment chatFragment = ChatFragment.newInstance(groupName, userName);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.billChatContainer, chatFragment)
                    .commit();
        }

        return view;
    }

    /**
     * Fetches bill information from the server and updates the UI with bill details.
     */
    private void loadBillInfo() {
        BillService service = RetrofitClient.getLocalClient().create(BillService.class);
        Call<Bill> call = service.getBillById(billId);

        call.enqueue(new Callback<Bill>() {
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bill bill = response.body();

                    billTitleTextView.setText(bill.getBillName());
                    billAmountTextView.setText("$" + bill.getBillAmount());
                    billDescriptionTextView.setText("Due: " + bill.getDueTime());
                    billStatusTextView.setText(bill.isPaid() ? "Paid ✅" : "Unpaid ❌");

                    if (bill.isPaid()) {
                        addMockTransaction(bill);
                        payButton.setEnabled(false);
                        payButton.setText("Already Paid");
                    }
                }
            }

            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Marks the bill as paid, updates the UI, and records a mock transaction.
     */
    private void markBillAsPaid() {
        payButton.setEnabled(false);
        payButton.setText("Paid ✅");
        billStatusTextView.setText("Paid ✅");

        String billName = billTitleTextView.getText().toString();
        String billAmount = billAmountTextView.getText().toString().replace("$", "");

        String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

        TransactionMockStorage.addTransaction(
                "Payment for " + billName,
                Double.parseDouble(billAmount),
                groupId,
                billId,
                currentDate
        );
    }

    /**
     * Adds a mock transaction for a bill that is already paid.
     *
     * @param bill the bill to create a transaction for
     */
    private void addMockTransaction(Bill bill) {
        String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

        TransactionMockStorage.addTransaction(
                "Payment for " + bill.getBillName(),
                Double.parseDouble(bill.getBillAmount()),
                groupId,
                billId,
                currentDate
        );
    }

    /**
     * Navigates back to the GroupDetailFragment with the current group ID.
     */
    private void navigateBackToGroup() {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("groupId", groupId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}