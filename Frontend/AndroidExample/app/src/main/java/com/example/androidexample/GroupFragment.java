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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupFragment extends Fragment {

    private TextView groupStatusTextView;
    private Button groupViewInvitesBtn;
    private static final String BASE_URL = "http://coms-3090-039.class.las.iastate.edu:8080/groups";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        groupStatusTextView = view.findViewById(R.id.groupStatusTextView);
        groupViewInvitesBtn = view.findViewById(R.id.viewInvitesButton);

        getGroup(2);
        //putGroup(2, "Car", 3);
        //postGroup(10, "Gas Money", 5);
        //deleteGroup(23);

        groupViewInvitesBtn.setOnClickListener(v ->{
            getGroupInv(2);
            //putGroupInv(2, "Car", 3);
            //postGroupInv(10, "Gas Money", 5);
            //deleteGroupInv(23);
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

    private void getGroupInv(int id) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BASE_URL + "/" + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String groupName = response.getString("user_name");
                        int capacity = response.getInt("group_id");
                        groupStatusTextView.setText("GET: " + groupName + " (Group ID: " + capacity + ")");
                    } catch (JSONException e) {
                        groupStatusTextView.setText("Failed to parse GET response");
                        e.printStackTrace();
                    }
                },
                error -> groupStatusTextView.setText("GET failed: " + error.getMessage())
        );

        queue.add(request);
    }

    private void putGroupInv(int id, String name, int capacity, String date) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BASE_URL + "/" + id;

        JSONObject putData = new JSONObject();
        try {
            putData.put("user_name", name);
            putData.put("group_id", capacity);
            putData.put("id", id);
            putData.put("date_created", date);
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

    private void postGroupInv(int id, String name, int capacity, String date) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BASE_URL + "/" + id;

        JSONObject postData = new JSONObject();
        try {
            postData.put("group_name", name);
            postData.put("capacity", capacity);
            postData.put("date_created", date);
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

    private void deleteGroupInv(int id) {
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
}