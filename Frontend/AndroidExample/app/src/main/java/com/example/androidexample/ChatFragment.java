package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for displaying a chat interface within a group.
 * <p>
 * Supports sending and receiving messages via a WebSocket connection,
 * displaying messages in a {@link RecyclerView} using {@link ChatAdapter}.
 * </p>
 */
public class ChatFragment extends Fragment {

    /** Argument key for the group name. */
    private static final String ARG_GROUP = "group_name";

    /** Argument key for the username of the current user. */
    private static final String ARG_USER = "username";

    /** Name of the chat group. */
    private String groupName;

    /** Username of the current user. */
    private String username;

    /** RecyclerView for displaying chat messages. */
    private RecyclerView recyclerView;

    /** Adapter for binding chat messages to the RecyclerView. */
    private ChatAdapter adapter;

    /** List of chat messages. */
    private List<Message> messageList;

    /** Input field for composing messages. */
    private EditText chatInput;

    /** Button for sending messages. */
    private ImageButton sendButton;

    /** Manager for handling WebSocket connections. */
    private WebSocketManager webSocketManager;

    /**
     * Creates a new instance of ChatFragment with the specified group name and username.
     *
     * @param groupName Name of the chat group
     * @param username  Username of the current user
     * @return A new {@link ChatFragment} instance
     */
    public static ChatFragment newInstance(String groupName, String username) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP, groupName);
        args.putString(ARG_USER, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupName = getArguments().getString(ARG_GROUP);
            username = getArguments().getString(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatInput = view.findViewById(R.id.chatInput);
        sendButton = view.findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        adapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(messageList.size() - 1);

        // Initialize WebSocketManager for real-time messaging
        webSocketManager = new WebSocketManager(groupName, username, (sender, messageText) -> {
            requireActivity().runOnUiThread(() -> addMessage(sender, messageText, R.drawable.profile_placeholder));
        });

        webSocketManager.connect();

        // Send button listener
        sendButton.setOnClickListener(v -> {
            String text = chatInput.getText().toString().trim();
            if (!text.isEmpty()) {
                webSocketManager.sendMessage(text);  // send to server
                addMessage(username, text, R.drawable.profile_placeholder);  // display locally
                chatInput.setText("");
            }
        });

        return view;
    }

    /**
     * Adds a new message to the chat and updates the RecyclerView.
     *
     * @param sender     Username of the sender
     * @param text       Content of the message
     * @param profileRes Resource ID of the sender's profile image
     */
    private void addMessage(String sender, String text, int profileRes) {
        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        String date = new SimpleDateFormat("MMM d", Locale.getDefault()).format(new Date());

        Message message = new Message(sender, text, time, date, profileRes);
        messageList.add(message);

        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
    }
}