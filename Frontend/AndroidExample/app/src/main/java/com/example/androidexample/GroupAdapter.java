package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    // Interface to handle clicks
    public interface OnGroupClickListener {
        void onGroupClick(JSONObject group);
    }

    private final List<JSONObject> groupList;
    private final OnGroupClickListener listener;

    public GroupAdapter(List<JSONObject> groupList, OnGroupClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameText, groupCapacityText;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameText = itemView.findViewById(R.id.groupNameText);
            groupCapacityText = itemView.findViewById(R.id.groupCapacityText);
        }
    }
}