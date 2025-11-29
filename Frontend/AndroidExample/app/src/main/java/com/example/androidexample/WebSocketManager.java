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

/**
 * Manages a WebSocket connection for a specific group and user.
 * <p>
 * Handles connecting, receiving messages, sending messages, and disconnecting.
 * Incoming messages are forwarded to a {@link MessageListener}.
 */
public class WebSocketManager {

    /**
     * Listener interface for receiving messages from the WebSocket.
     */
    public interface MessageListener {
        /**
         * Called when a new message is received.
         *
         * @param sender The username of the sender.
         * @param text   The text of the message.
         */
        void onMessageReceived(String sender, String text);
    }

    /** The WebSocket instance. */
    private WebSocket webSocket;

    /** Listener to receive messages from the WebSocket. */
    private MessageListener listener;

    /** OkHttpClient used to manage the WebSocket connection. */
    private OkHttpClient client;

    /** WebSocket URL for the specific group and user. */
    private String url;

    /**
     * Constructs a new WebSocketManager.
     *
     * @param groupName Name of the group to connect to.
     * @param username  Username of the current user.
     * @param listener  Listener to handle incoming messages.
     */
    public WebSocketManager(String groupName, String username, MessageListener listener) {
        this.listener = listener;
        this.client = new OkHttpClient();
        this.url = "ws://10.0.2.2:8080/GroupServer/" + groupName + "/" + username;
    }

    /**
     * Connects to the WebSocket server and sets up message handling.
     */
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

    /**
     * Sends a message through the WebSocket connection.
     *
     * @param message The message text to send.
     */
    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    /**
     * Disconnects from the WebSocket server.
     */
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Disconnected");
        }
    }
}