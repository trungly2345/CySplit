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

public class ChatFragment extends Fragment {

    private static final String ARG_GROUP = "group_name";
    private static final String ARG_USER = "username";

    private String groupName;
    private String username;

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Message> messageList;

    private EditText chatInput;
    private ImageButton sendButton;

    private WebSocketManager webSocketManager;

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

        // Initialize WebSocketManager
        webSocketManager = new WebSocketManager(groupName, username, (sender, messageText) -> {
            requireActivity().runOnUiThread(() -> addMessage(sender, messageText, R.drawable.profile_placeholder));
        });

        webSocketManager.connect();

        sendButton.setOnClickListener(v -> {
            String text = chatInput.getText().toString().trim();
            if (!text.isEmpty()) {
                // Send to server
                webSocketManager.sendMessage(text);
                // Display locally
                addMessage(username, text, R.drawable.profile_placeholder);
                chatInput.setText("");
            }
        });

        return view;
    }

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