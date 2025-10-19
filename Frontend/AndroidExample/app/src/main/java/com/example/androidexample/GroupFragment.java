package com.example.androidexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static final String BASE_URL = "http://coms-3090-039.class.las.iastate.edu:8080/groups";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        groupStatusTextView = view.findViewById(R.id.groupStatusTextView);

        //getGroup(8);
        postGroup(1, "Hells Kitchen Las Vegas Summer 2027", 6);
        //postGroup(9, "Gas Money", 5);
        //deleteGroup(23);

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
}