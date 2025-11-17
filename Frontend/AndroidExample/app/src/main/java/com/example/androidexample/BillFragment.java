package com.example.androidexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BillFragment extends Fragment {

    private int transactionId;
    private TextView billTextView;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);
        billTextView = view.findViewById(R.id.billTextView);

        if (getArguments() != null) {
            transactionId = getArguments().getInt("transactionId");
            fetchBill();
        }

        return view;
    }

    private void fetchBill() {
        executor.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/bill/" + transactionId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                InputStream inputStream = conn.getInputStream();
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                JSONObject billJson = new JSONObject(response);

                // Update UI on main thread
                mainHandler.post(() -> {
                    try {
                        billTextView.setText(billJson.toString(2));
                    } catch (Exception e) {
                        e.printStackTrace();
                        billTextView.setText("Error displaying bill");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> billTextView.setText("Failed to load bill"));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow(); // clean up the executor
    }
}