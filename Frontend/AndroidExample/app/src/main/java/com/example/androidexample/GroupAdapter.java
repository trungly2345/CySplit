package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of groups.
 * <p>
 * Each group is represented as a {@link JSONObject} and displayed with its name and capacity.
 * The adapter uses a click listener interface {@link OnGroupClickListener} to handle item clicks.
 * </p>
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    /**
     * Interface for handling click events on a group item.
     */
    public interface OnGroupClickListener {
        /**
         * Called when a group item is clicked.
         *
         * @param group the JSONObject representing the clicked group
         */
        void onGroupClick(JSONObject group);
    }

    /** List of group data represented as JSONObjects. */
    private final List<JSONObject> groupList;

    /** Listener to handle click events on group items. */
    private final OnGroupClickListener listener;

    /**
     * Creates a new GroupAdapter with the given group data and click listener.
     *
     * @param groupList list of groups to display
     * @param listener  listener to handle clicks on group items
     */
    public GroupAdapter(List<JSONObject> groupList, OnGroupClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }

    /**
     * Creates a new {@link GroupViewHolder} for a group item.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new view
     * @return a new GroupViewHolder
     */
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    /**
     * Binds a group to the ViewHolder at the specified position.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        JSONObject group = groupList.get(position);
        try {
            holder.groupNameText.setText(group.getString("group_name"));
            holder.groupCapacityText.setText("Capacity: " + group.getInt("capacity"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> listener.onGroupClick(group));
    }

    /**
     * Returns the total number of groups in the adapter.
     *
     * @return the number of groups
     */
    @Override
    public int getItemCount() {
        return groupList.size();
    }

    /**
     * ViewHolder class for displaying a single group item in the RecyclerView.
     */
    static class GroupViewHolder extends RecyclerView.ViewHolder {

        /** TextView displaying the group's name. */
        TextView groupNameText;

        /** TextView displaying the group's capacity. */
        TextView groupCapacityText;

        /**
         * Constructs a new GroupViewHolder.
         *
         * @param itemView the view for the group item
         */
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameText = itemView.findViewById(R.id.groupNameText);
            groupCapacityText = itemView.findViewById(R.id.groupCapacityText);
        }
    }
}