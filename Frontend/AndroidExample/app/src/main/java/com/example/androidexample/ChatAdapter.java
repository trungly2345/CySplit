package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * RecyclerView Adapter for displaying chat messages.
 * <p>
 * Each message displays the sender's profile image, username, timestamp, and message text.
 * </p>
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    /** List of messages to display in the chat. */
    private List<Message> messages;

    /**
     * Constructs a ChatAdapter with a list of messages.
     *
     * @param messages List of {@link Message} objects to display
     */
    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * ViewHolder class for displaying an individual chat message.
     */
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        /** Profile image of the message sender. */
        ImageView profileImage;

        /** Username of the message sender. */
        TextView username;

        /** Timestamp of when the message was sent. */
        TextView messageTime;

        /** Text content of the message. */
        TextView messageText;

        /**
         * Constructs a new MessageViewHolder.
         *
         * @param itemView The item view representing the chat message
         */
        public MessageViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            username = itemView.findViewById(R.id.username);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }

    /**
     * Inflates the layout for a message item and returns a new ViewHolder.
     *
     * @param parent   The parent ViewGroup
     * @param viewType The view type
     * @return A new {@link MessageViewHolder}
     */
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    /**
     * Binds a message to the ViewHolder, setting username, message text, timestamp, and profile image.
     *
     * @param holder   The ViewHolder to bind
     * @param position Position in the message list
     */
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.username.setText(message.getUsername());
        holder.messageText.setText(message.getText());
        String timeAndDate = message.getTime() + " • " + message.getDate();
        holder.messageTime.setText(timeAndDate);
        holder.profileImage.setImageResource(message.getProfileRes());
    }

    /**
     * Returns the total number of messages in the list.
     *
     * @return Number of messages
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }
}