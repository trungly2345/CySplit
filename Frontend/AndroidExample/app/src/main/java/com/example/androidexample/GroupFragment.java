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

/**
 * Fragment that displays a list of groups retrieved from a server.
 * <p>
 * Uses a {@link RecyclerView} to show the group data and {@link GroupAdapter} to handle the display.
 * Data is fetched from a remote server via {@link GroupService} using Retrofit.
 * Clicking on a group opens {@link GroupDetailFragment} with the selected group's details.
 * </p>
 */
public class GroupFragment extends Fragment {

    /** RecyclerView that displays the list of groups. */
    private RecyclerView groupRecyclerView;

    /** List containing group data represented as JSONObjects. */
    private final List<JSONObject> groupList = new ArrayList<>();

    /** Adapter for binding group data to the RecyclerView. */
    private GroupAdapter adapter;

    /** Retrofit service interface for fetching group data from the server. */
    private GroupService groupService;

    /**
     * Called to create the view hierarchy associated with the fragment.
     *
     * @param inflater LayoutInflater object that can be used to inflate any views
     * @param container Parent view that the fragment's UI should attach to
     * @param savedInstanceState Bundle containing saved state (if any)
     * @return the root view of the fragment layout
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        // Initialize RecyclerView
        groupRecyclerView = view.findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with click listener
        adapter = new GroupAdapter(groupList, this::onGroupClick);
        groupRecyclerView.setAdapter(adapter);

        // Initialize Retrofit service
        groupService = RetrofitClient.getRemoteClient().create(GroupService.class);

        // Load group data from server (example group ID 3)
        loadGroupFromServer(3);

        return view;
    }

    /**
     * Loads group data from the server for the specified group ID.
     * Updates the RecyclerView with the fetched data.
     *
     * @param groupId the ID of the group to fetch from the server
     */
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

    /**
     * Handles clicks on a group item in the RecyclerView.
     * Opens {@link GroupDetailFragment} with details of the selected group.
     *
     * @param group the JSONObject representing the clicked group
     */
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