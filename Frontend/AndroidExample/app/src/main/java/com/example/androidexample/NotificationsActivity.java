package com.example.androidexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//CI/CD test

/**
 * Activity to display user notifications in a RecyclerView.
 * Fetches notifications from the server and allows viewing them.
 * Includes a test notification for demonstration purposes.
 */
public class NotificationsActivity extends AppCompatActivity {

    /**
     * RecyclerView displaying the list of notifications.
     */
    private RecyclerView recyclerView;

    /**
     * ProgressBar displayed while loading notifications.
     */
    private ProgressBar progressBar;

    /**
     * TextView shown when there are no notifications.
     */
    private TextView emptyText;

    /**
     * Adapter for the RecyclerView to display notifications.
     */
    private NotificationAdapter adapter;

    /**
     * List storing the notifications.
     */
    private final List<NotificationItem> notifications = new ArrayList<>();

    /**
     * Volley RequestQueue for network requests.
     */
    private RequestQueue requestQueue;

    /**
     * Base URL for fetching notifications from the server.
     */
    private final String BASE_URL = "http://coms-3090-039.class.las.iastate.edu:8080/groups";

    /**
     * Called when the activity is first created.
     * Initializes the RecyclerView, adapter, network queue, and starts fetching notifications.
     *
     * @param savedInstanceState Saved state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ImageView backArrow = findViewById(R.id.back_arrow);
        recyclerView = findViewById(R.id.notifications_recycler);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.empty_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        backArrow.setOnClickListener(v -> onBackPressed());

        fetchNotifications();
        addTestNotification();
    }

    /**
     * Adds a test notification to the top of the notification list.
     */
    private void addTestNotification() {
        NotificationItem test = new NotificationItem(
                "Test Notification",
                "hi",
                "just now"
        );
        notifications.add(0, test);
        adapter.notifyItemInserted(0);
    }


    /**
     * Fetches notifications for the currently logged-in user from the server.
     * Updates the RecyclerView when the notifications are received.
     * Shows a progress bar while loading and handles errors.
     */
    private void fetchNotifications() {
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String url = BASE_URL + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    notifications.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            notifications.add(new NotificationItem(
                                    obj.optString("title"),
                                    obj.optString("message"),
                                    obj.optString("timestamp")
                            ));
                        }

                        adapter.notifyDataSetChanged();
                        emptyText.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);

                    } catch (JSONException e) {
                        Toast.makeText(NotificationsActivity.this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String errorMsg = error.getMessage();
                    if (error.networkResponse != null) {
                        errorMsg = "Error " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(NotificationsActivity.this, "Failed: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }
}
