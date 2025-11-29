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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private NotificationAdapter adapter;
    private final List<NotificationItem> notifications = new ArrayList<>();

    private RequestQueue requestQueue;
    private WebSocket webSocket;

    private final String BASE_URL = "http://10.0.2.2:3004";
    private final String WEBSOCKET_URL = "ws://10.0.2.2:8081/NotificationServer/";

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
        setUpWebSocket();
    }

    private void addTestNotification() {
        NotificationItem test = new NotificationItem(
                "Test Notification",
                "This is a test notification",
                "Just now"
        );
        notifications.add(0, test);
        adapter.notifyItemInserted(0);
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

        JsonArrayRequest request = new JsonArrayRequest(
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
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String msg = (error.networkResponse != null)
                            ? "Error " + error.networkResponse.statusCode
                            : "Network error";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    private void setUpWebSocket() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in for WebSocket", Toast.LENGTH_SHORT).show();
            return;
        }

        String websocketUrl = WEBSOCKET_URL + userId;

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(websocketUrl).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                runOnUiThread(() -> {
                    try {
                        JSONObject obj = new JSONObject(text);

                        NotificationItem item = new NotificationItem(
                                obj.optString("title"),
                                obj.optString("message"),
                                obj.optString("timestamp")
                        );

                        notifications.add(0, item);
                        adapter.notifyItemInserted(0);

                    } catch (JSONException e) {
                        Log.e("WebSocket", "JSON parse error", e);
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                // Ignored (binary messages)
            }

            @Override
            public void onClosed(WebSocket ws, int code, String reason) {
                Log.d("WebSocket", "Closed: " + reason);
            }

            @Override
            public void onFailure(WebSocket ws, Throwable t, okhttp3.Response resp) {
                Log.e("WebSocket", "Failure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null)
            webSocket.close(1000, "Closing");
    }
}
