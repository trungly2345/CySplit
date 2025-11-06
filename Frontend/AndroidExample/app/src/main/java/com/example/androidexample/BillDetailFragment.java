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

        if (getArguments() != null) {
            billId = getArguments().getInt("billId");
            loadBillInfo();
        }

        payButton.setOnClickListener(v -> markBillAsPaid());

        backButton.setOnClickListener(v -> {
            // Create a new instance of GroupDetailFragment
            GroupDetailFragment fragment = new GroupDetailFragment();

            // Pass the groupId back
            Bundle bundle = new Bundle();
            bundle.putInt("groupId", groupId);  // make sure groupId is stored in this fragment
            fragment.setArguments(bundle);

            // Replace current fragment with GroupDetailFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // optional, allows "back" button to work
                    .commit();
        });

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

                    // If it’s already paid from the backend, we’ll still show the transaction
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
        // Disable button immediately to prevent duplicates
        payButton.setEnabled(false);
        payButton.setText("Paid ✅");
        billStatusTextView.setText("Paid ✅");

        // Add to mock transactions (even though backend doesn't persist yet)
        String billName = billTitleTextView.getText().toString();
        String billAmount = billAmountTextView.getText().toString().replace("$", "");

        // Get current date
        String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

        TransactionMockStorage.addTransaction(
                "Payment for " + billName,
                Double.parseDouble(billAmount),
                groupId,
                billId,
                currentDate  // <-- pass date here
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