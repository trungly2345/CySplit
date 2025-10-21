package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment {

    private int transactionId;
    private LinearLayout messageContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        transactionId = getArguments().getInt("transactionId");

        TextView chatTitle = view.findViewById(R.id.chatTitle);
        messageContainer = view.findViewById(R.id.messageContainer);
        EditText messageInput = view.findViewById(R.id.messageInput);
        ImageButton sendButton = view.findViewById(R.id.sendButton);

        chatTitle.setText("Chat for Transaction #" + transactionId);

        // Mock sending — later this becomes your WebSocket send()
        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                addMessage("You: " + msg);
                messageInput.setText("");
            }
        });

        return view;
    }

    private void addMessage(String text) {
        TextView msgView = new TextView(getContext());
        msgView.setText(text);
        msgView.setPadding(8, 8, 8, 8);
        messageContainer.addView(msgView);
    }
}