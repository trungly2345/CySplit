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

/**
 * Fragment that displays detailed information about a specific bill.
 * <p>
 * The fragment fetches bill data asynchronously from a remote server based on a given transaction ID.
 * The bill details are displayed in a TextView. Networking is handled using a background ExecutorService,
 * and UI updates are posted to the main thread via a Handler.
 * </p>
 */
public class BillFragment extends Fragment {

    /** The ID of the transaction to fetch the bill for. */
    private int transactionId;

    /** TextView used to display the bill information. */
    private TextView billTextView;

    /** ExecutorService for performing network requests on a background thread. */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /** Handler for posting UI updates to the main thread. */
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Called to create the fragment's view hierarchy.
     *
     * @param inflater           LayoutInflater to inflate views
     * @param container          Parent view that the fragment's UI should attach to
     * @param savedInstanceState Saved state bundle
     * @return The root view of the fragment
     */
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

    /**
     * Fetches bill data from the server asynchronously and updates the UI with the result.
     */
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

    /**
     * Cleans up resources when the fragment's view is destroyed.
     * Shuts down the ExecutorService to prevent memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }
}