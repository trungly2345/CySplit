package com.example.androidexample;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {

    public interface MessageListener {
        void onMessageReceived(String sender, String text);
    }

    private WebSocket webSocket;
    private MessageListener listener;
    private OkHttpClient client;
    private String url;

    public WebSocketManager(String groupName, String username, MessageListener listener) {
        this.listener = listener;
        this.client = new OkHttpClient();
        this.url = "ws://10.0.2.2:8080/GroupServer/" + groupName + "/" + username;
    }

    public void connect() {
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d("WebSocketManager", "Connected to " + url);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                try {
                    JSONObject json = new JSONObject(text);
                    String sender = json.optString("user", "Server");
                    String messageText = json.optString("text", text);

                    if (listener != null) {
                        listener.onMessageReceived(sender, messageText);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onMessageReceived("Server", text);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.e("WebSocketManager", "Error: " + t.getMessage(), t);
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                webSocket.close(1000, null);
                Log.d("WebSocketManager", "Closing: " + reason);
            }
        });
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Disconnected");
        }
    }
}