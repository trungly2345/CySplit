package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    private RecyclerView groupRecyclerView;
    private final List<JSONObject> groupList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        groupRecyclerView = view.findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        GroupAdapter adapter = new GroupAdapter(groupList, this::onGroupClick);
        groupRecyclerView.setAdapter(adapter);

        //TODO: TEMP: mock data until WebSocket pushes groups
        addMockGroups(adapter);

        return view;
    }

    private void addMockGroups(GroupAdapter adapter) {
        try {
            for (int i = 1; i <= 5; i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", i);
                obj.put("group_name", "Group " + i);
                obj.put("capacity", 3 + i);
                groupList.add(obj);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onGroupClick(JSONObject group) {
        try {
            int groupId = group.getInt("id");
            GroupDetailFragment fragment = new GroupDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("groupId", groupId);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}