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

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Message> messageList;

    private EditText chatInput;
    private ImageButton sendButton;

    private WebSocketManager webSocketManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatInput = view.findViewById(R.id.chatInput);
        sendButton = view.findViewById(R.id.sendButton);

        // Example initial messages
        messageList = new ArrayList<>();
        messageList.add(new Message("Alice", "Hello!", "12:01 PM", "Nov 5", R.drawable.profile_placeholder));
        messageList.add(new Message("Bob", "Hi Alice!", "12:02 PM", "Nov 5", R.drawable.profile_placeholder));

        adapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(adapter);

        // Scroll to bottom initially
        recyclerView.scrollToPosition(messageList.size() - 1);

        webSocketManager = new WebSocketManager(message -> {
            requireActivity().runOnUiThread(() ->{
                addMessage("Server", message, R.drawable.profile_placeholder);
            });
        });

        webSocketManager.connect();

        sendButton.setOnClickListener(v -> {
            String text = chatInput.getText().toString().trim();
            if (!text.isEmpty()) {
                // Send via WebSocket
                webSocketManager.sendMessage(text);

                // Add locally
                addMessage("You", text, R.drawable.profile_placeholder);
                chatInput.setText("");
            }
        });

        return view;
    }

    private void addMessage(String username, String text, int profileRes) {
        // Get current time
        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        // Get current date
        String date = new SimpleDateFormat("MMM d", Locale.getDefault()).format(new Date());

        Message message = new Message(username, text, time, date, profileRes);
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
