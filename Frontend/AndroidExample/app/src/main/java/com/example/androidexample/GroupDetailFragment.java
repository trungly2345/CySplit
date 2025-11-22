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

/**
 * Fragment that displays detailed information about a specific group.
 * <p>
 * Shows group name, capacity, and the most recent bill.
 * Provides buttons to view all bills, view transactions, and navigate to bill details.
 * Group and bill data are fetched via Retrofit from remote and local servers.
 * </p>
 */
public class GroupDetailFragment extends Fragment {

    /** ID of the group being displayed. */
    private int groupId;

    /** TextView displaying the group name. */
    private TextView groupNameTextView;

    /** TextView displaying group description (e.g., capacity). */
    private TextView groupDescriptionTextView;

    /** Container for the most recent bill card. */
    private CardView recentBillContainer;

    /** TextView displaying the title/name of the most recent bill. */
    private TextView recentBillTitle;

    /** TextView displaying the amount of the most recent bill. */
    private TextView recentBillAmount;

    /** Button to view all bills in the group. */
    private Button buttonViewAllBills;

    /** Button to view all transactions related to the group. */
    private Button buttonViewTransactions;

    /** ID of the most recent bill. */
    private int recentBillId;

    /**
     * Called to create the fragment view.
     *
     * @param inflater LayoutInflater to inflate views
     * @param container Parent view that fragment UI will attach to
     * @param savedInstanceState Bundle containing saved state (if any)
     * @return the root view for the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);

        // Initialize UI elements
        groupNameTextView = view.findViewById(R.id.groupNameTextView);
        groupDescriptionTextView = view.findViewById(R.id.groupDescriptionTextView);
        recentBillContainer = view.findViewById(R.id.recentBillContainer);
        recentBillTitle = view.findViewById(R.id.recentBillTitle);
        recentBillAmount = view.findViewById(R.id.recentBillAmount);
        buttonViewAllBills = view.findViewById(R.id.buttonViewAllBills);
        buttonViewTransactions = view.findViewById(R.id.buttonViewTransactions);

        // Get groupId from arguments
        groupId = getArguments() != null ? getArguments().getInt("groupId") : -1;

        // Load group and recent bill data
        loadGroupInfo();
        loadRecentBill();

        // Set click listeners
        recentBillContainer.setOnClickListener(v -> openBillDetail(recentBillId, groupId));
        buttonViewAllBills.setOnClickListener(v -> openAllBills());
        buttonViewTransactions.setOnClickListener(v -> openTransactions());

        return view;
    }

    /**
     * Fetches group information from the server and updates UI.
     */
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

    /**
     * Fetches the most recent bill from the local server and updates the UI.
     */
    private void loadRecentBill() {
        BillService billService = RetrofitClient.getLocalClient().create(BillService.class);
        Call<Bill> call = billService.getBillById(1); // Placeholder for now

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

    /**
     * Sets fallback values for the most recent bill in case of network failure.
     */
    private void setRecentBillFallback() {
        recentBillId = 1;
        recentBillTitle.setText("Bill 1");
        recentBillAmount.setText("$0.00");
    }

    /**
     * Opens the BillDetailFragment for a specific bill and group.
     *
     * @param billId  the ID of the bill to view
     * @param groupId the ID of the group the bill belongs to
     */
    private void openBillDetail(int billId, int groupId) {
        BillDetailFragment fragment = new BillDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("billId", billId);
        bundle.putInt("groupId", groupId);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Opens the BillsListFragment to view all bills in the group.
     */
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

    /**
     * Opens the TransactionFragment to view all transactions for the group.
     */
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