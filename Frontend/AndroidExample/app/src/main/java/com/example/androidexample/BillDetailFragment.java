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

public class BillDetailFragment extends Fragment {

    private int billId;
    private int groupId = 1; // mock for now
    private String userName = "";

    private TextView billTitleTextView, billAmountTextView, billDescriptionTextView, billStatusTextView;
    private Button payButton, backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_detail, container, false);

        billTitleTextView = view.findViewById(R.id.billTitleTextView);
        billAmountTextView = view.findViewById(R.id.billAmountTextView);
        billDescriptionTextView = view.findViewById(R.id.billDescriptionTextView);
        billStatusTextView = view.findViewById(R.id.billStatusTextView);
        payButton = view.findViewById(R.id.payButton);
        backButton = view.findViewById(R.id.backToGroupButton);

        // Get arguments if any
        if (getArguments() != null) {
            billId = getArguments().getInt("billId");
            groupId = getArguments().getInt("groupId", 1); // default to 1 if not passed
            userName = getArguments().getString("userName", "Aaron"); // default name

            loadBillInfo();
        }

        payButton.setOnClickListener(v -> markBillAsPaid());

        backButton.setOnClickListener(v -> {
            GroupDetailFragment fragment = new GroupDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("groupId", groupId);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Add ChatFragment dynamically with group name & username
        if (savedInstanceState == null) { // prevent duplicates on rotation
            userName = UserSession.getInstance().getUsername();

            String groupName = "Group" + groupId;
            ChatFragment chatFragment = ChatFragment.newInstance(groupName, userName);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.billChatContainer, chatFragment)
                    .commit();
        }

        return view;
    }

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
}