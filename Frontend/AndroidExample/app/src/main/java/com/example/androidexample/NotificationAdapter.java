package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of notifications.
 * <p>
 * Each notification can be expanded or collapsed by tapping on it, showing or hiding the full message content.
 * </p>
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    /** List of notifications to display. */
    private final List<NotificationItem> notifications;

    /**
     * Constructs a new NotificationAdapter.
     *
     * @param notifications the list of notifications to display
     */
    public NotificationAdapter(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }

    /**
     * ViewHolder class for a single notification item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, timestamp;
        View messageContainer;
        View card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            timestamp = itemView.findViewById(R.id.notification_timestamp);
            messageContainer = itemView.findViewById(R.id.message_container);
            card = itemView.findViewById(R.id.notification_card);
        }
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notifications.get(position);

        holder.title.setText(item.getTitle());
        holder.message.setText(item.getMessage());
        holder.timestamp.setText(item.getTimestamp());

        boolean expanded = item.isExpanded();
        holder.messageContainer.setVisibility(expanded ? View.VISIBLE : View.GONE);

        holder.card.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
