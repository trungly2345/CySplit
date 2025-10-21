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
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {

    private int groupId;
    private RecyclerView recyclerView;
    private final List<JSONObject> transactionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        recyclerView = view.findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupId = getArguments().getInt("groupId");

        TransactionAdapter adapter = new TransactionAdapter(transactionList, this::onTransactionClick);
        recyclerView.setAdapter(adapter);

        //TODO: TEMP mock data — later, populate via WebSocket
        addMockTransactions(adapter);

        return view;
    }

    private void addMockTransactions(TransactionAdapter adapter) {
        try {
            for (int i = 1; i <= 4; i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", i);
                obj.put("name", "Transaction " + i + " (Group " + groupId + ")");
                obj.put("amount", i * 10);
                transactionList.add(obj);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onTransactionClick(JSONObject transaction) {
        try {
            int transactionId = transaction.getInt("id");
            ChatFragment fragment = new ChatFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("transactionId", transactionId);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}