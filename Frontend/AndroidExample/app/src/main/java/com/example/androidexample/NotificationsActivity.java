package com.example.androidexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private NotificationAdapter adapter;
    private final List<NotificationItem> notifications = new ArrayList<>();

    private RequestQueue requestQueue;
    private WebSocket webSocket;

    // Replace this with the actual base URL for your API
    private final String BASE_URL = "http://localhost:3004";
    private final String WEBSOCKET_URL = "ws://localhost:8081/NotificationServer/";  // WebSocket URL (use actual WebSocket server URL)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize views
        ImageView backArrow = findViewById(R.id.back_arrow);
        recyclerView = findViewById(R.id.notifications_recycler);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.empty_text);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Set back arrow click listener
        backArrow.setOnClickListener(v -> onBackPressed());

        // Fetch initial notifications (this will display notifications on the page)
        fetchNotifications();

        // Add a test notification to simulate a new notification (for testing UI)
        addTestNotification();

        // Set up WebSocket connection
        setUpWebSocket();
    }

    private void addTestNotification() {
        Log.d("NotificationsActivity", "Adding test notification");
        NotificationItem test = new NotificationItem(
                "Test Notification",
                "This is a test notification",
                "Just now"
        );
        notifications.add(0, test);
        adapter.notifyItemInserted(0);  // Notify the adapter that a new item has been added at position 0
    }

    private void fetchNotifications() {
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String url = BASE_URL + "/notifications/user/" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    notifications.clear();

                    try {
                        // Parse the response and add notifications to the list
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            notifications.add(new NotificationItem(
                                    obj.optString("title"),
                                    obj.optString("message"),
                                    obj.optString("timestamp")
                            ));
                        }

                        // Log the notifications for debugging
                        Log.d("NotificationsActivity", "Notifications fetched: " + notifications.size());

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

    // WebSocket setup for real-time notifications
    private void setUpWebSocket() {
        // Get user ID for WebSocket connection
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in for WebSocket", Toast.LENGTH_SHORT).show();
            return;
        }

        String websocketUrl = WEBSOCKET_URL + userId;

        // Set up WebSocket client
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(websocketUrl).build();

        // Create WebSocket connection
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                // Handle incoming message (notification)
                runOnUiThread(() -> {
                    // Parse the incoming message as a notification and add to the list
                    try {
                        JSONObject notificationObj = new JSONObject(text);
                        String title = notificationObj.optString("title");
                        String message = notificationObj.optString("message");
                        String timestamp = notificationObj.optString("timestamp");

                        NotificationItem newNotification = new NotificationItem(title, message, timestamp);
                        notifications.add(0, newNotification);
                        adapter.notifyItemInserted(0);
                    } catch (JSONException e) {
                        Log.e("WebSocket", "Failed to parse message", e);
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d("WebSocket", "Connection closed: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                super.onFailure(webSocket, t, response);
                Log.e("WebSocket", "WebSocket failure: " + t.getMessage(), t);
            }
        });

        // Close WebSocket when activity is destroyed
        client.dispatcher().executorService().shutdown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Closing WebSocket");
        }
    }
}
