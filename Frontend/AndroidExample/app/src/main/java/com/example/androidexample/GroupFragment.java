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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupFragment extends Fragment {

    private RecyclerView groupRecyclerView;
    private final List<JSONObject> groupList = new ArrayList<>();
    private GroupAdapter adapter;
    private GroupService groupService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        groupRecyclerView = view.findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GroupAdapter(groupList, this::onGroupClick);
        groupRecyclerView.setAdapter(adapter);

        // initialize service
        groupService = RetrofitClient.getRemoteClient().create(GroupService.class);

        // load group from server
        loadGroupFromServer(3); // You can change 3 to dynamic later

        return view;
    }

    private void loadGroupFromServer(int groupId) {
        groupService.getGroupById(groupId).enqueue(new Callback<Group>() {
            @Override
            public void onResponse(@NonNull Call<Group> call, @NonNull Response<Group> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Group group = response.body();
                        JSONObject obj = new JSONObject();
                        obj.put("id", group.getId());
                        obj.put("group_name", group.getGroupName());
                        obj.put("capacity", group.getCapacity());

                        groupList.clear();
                        groupList.add(obj);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Group> call, @NonNull Throwable t) {
                t.printStackTrace();
                System.out.println("Network error: " + t.getMessage());
            }
        });
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