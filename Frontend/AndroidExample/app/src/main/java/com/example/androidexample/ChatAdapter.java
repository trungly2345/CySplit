package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> messages;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView username, messageTime, messageText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            username = itemView.findViewById(R.id.username);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.username.setText(message.getUsername());
        holder.messageText.setText(message.getText());
        String timeAndDate = message.getTime() + " • " + message.getDate();
        holder.messageTime.setText(timeAndDate);
        holder.profileImage.setImageResource(message.getProfileRes());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}