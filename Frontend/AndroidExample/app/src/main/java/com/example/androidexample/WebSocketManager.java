package com.example.androidexample;

import android.util.Log;
import okhttp3.*;

public class WebSocketManager {

    private static final String TAG = "WebSocketManager";
    private static final String SOCKET_URL = "ws://coms-3090-039.class.las.iastate.edu:8080/chat";
    // TODO: ^ adjust endpoint later

    private OkHttpClient client;
    private WebSocket webSocket;
    private WebSocketListener listener;

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    private MessageListener messageListener;

    public WebSocketManager(MessageListener messageListener) {
        this.messageListener = messageListener;
        this.client = new OkHttpClient();
    }

    public void connect() {
        Request request = new Request.Builder().url(SOCKET_URL).build();
        listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "Connected to WebSocket");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Message received: " + text);
                if (messageListener != null) {
                    messageListener.onMessageReceived(text);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket error", t);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + reason);
            }
        };
        webSocket = client.newWebSocket(request, listener);
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        } else {
            Log.e(TAG, "WebSocket not connected!");
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "User left chat");
            webSocket = null;
        }
    }
}
