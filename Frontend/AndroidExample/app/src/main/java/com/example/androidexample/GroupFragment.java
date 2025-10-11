package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupFragment extends Fragment {

    private TextView groupStatusTextView;
    private Button groupViewInvitesBtn;
    private static final String BASE_URL = "http://coms-3090-039.class.las.iastate.edu:8080/groups";
    private static final String INV_URL = "http://coms-3090-039.class.las.iastate.edu:8080/invitations";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        groupStatusTextView = view.findViewById(R.id.groupStatusTextView);
        groupViewInvitesBtn = view.findViewById(R.id.viewInvitesButton);

        //getGroup(2);
        //putGroup(2, "Car", 3);
        //postGroup(10, "Gas Money", 5);
        //deleteGroup(23);

        groupViewInvitesBtn.setOnClickListener(v ->{
            //getGroupInv();
            //putGroupInv(4, "CarMan123");
            //postGroupInv(1, "BigDiscGolfGuy");
            //deleteGroupInv(3);
        });

        return view;
    }

    private void getGroup(int id) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BASE_URL + "/" + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String groupName = response.getString("group_name");
                        int capacity = response.getInt("capacity");
                        groupStatusTextView.setText("GET: " + groupName + " (Capacity: " + capacity + ")");
                    } catch (JSONException e) {
                        groupStatusTextView.setText("Failed to parse GET response");
                        e.printStackTrace();
                    }
                },
                error -> groupStatusTextView.setText("GET failed: " + error.getMessage())
        );

        queue.add(request);
    }

    private void putGroup(int id, String name, int capacity) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BASE_URL + "/" + id;

        JSONObject putData = new JSONObject();
        try {
            putData.put("group_name", name);
            putData.put("capacity", capacity);
            putData.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                putData,
                response -> groupStatusTextView.setText("PUT: Updated to " + name),
                error -> groupStatusTextView.setText("PUT failed: " + error.getMessage())
        );

        queue.add(request);
    }

    private void postGroup(int id, String name, int capacity) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BASE_URL + "/" + id;

        JSONObject postData = new JSONObject();
        try {
            postData.put("group_name", name);
            postData.put("capacity", capacity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                response -> groupStatusTextView.setText("POST: " + name),
                error -> groupStatusTextView.setText("POST failed: " + error.getMessage())
        );

        queue.add(request);
    }

    private void deleteGroup(int id) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BASE_URL + "/" + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> groupStatusTextView.setText("DELETE: Removed group " + id),
                error -> groupStatusTextView.setText("DELETE failed: " + error.getMessage())
        );

        queue.add(request);
    }

    private void getGroupInv() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = INV_URL;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        StringBuilder sb = new StringBuilder("Invites:\n");
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String userName = obj.getString("userName");
                            String dateCreated = obj.getString("dateCreated");
                            sb.append("• ID: ").append(id)
                                    .append(", User: ").append(userName)
                                    .append(", Date: ").append(dateCreated)
                                    .append("\n");
                        }
                        groupStatusTextView.setText(sb.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        groupStatusTextView.setText("Failed to parse invitations");
                    }
                },
                error -> groupStatusTextView.setText("GET failed: " + error.toString())
        );

        queue.add(request);
    }

    private void putGroupInv(int invitationId, String userName) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = INV_URL + "/" + invitationId;

        JSONObject putData = new JSONObject();
        try {
            putData.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                putData,
                response -> groupStatusTextView.setText("PUT: Updated invite " + invitationId + " to user " + userName),
                error -> groupStatusTextView.setText("PUT failed: " + error.toString())
        );

        queue.add(request);
    }

    private void postGroupInv(int groupId, String userName) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BASE_URL + "/" + groupId + "/invitations";

        JSONObject postData = new JSONObject();
        try {
            postData.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                response -> groupStatusTextView.setText("POST: Invite sent to " + userName),
                error -> groupStatusTextView.setText("POST failed: " + error.toString())
        );

        queue.add(request);
    }

    private void deleteGroupInv(int invitationId) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = INV_URL + "/" + invitationId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, // no body for delete
                response -> groupStatusTextView.setText("DELETE: Removed invite " + invitationId),
                error -> groupStatusTextView.setText("DELETE failed: " + error.toString())
        );

        queue.add(request);
    }
}