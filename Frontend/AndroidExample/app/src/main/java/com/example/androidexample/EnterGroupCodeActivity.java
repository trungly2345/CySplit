package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class EnterGroupCodeActivity extends AppCompatActivity {

    private EditText codeInput;
    private Button joinButton;

    private final String JOIN_GROUP_URL = "http://localhost:8080/onetimegroup/code/";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_group_code);

        requestQueue = Volley.newRequestQueue(this);

        codeInput = findViewById(R.id.group_code_input);
        joinButton = findViewById(R.id.join_group_button);

        joinButton.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim();

            if (code.isEmpty()) {
                Toast.makeText(this, "Please enter a code", Toast.LENGTH_SHORT).show();
                return;
            }

            joinGroupWithCode(code);
        });
    }

    private void joinGroupWithCode(String code) {

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User session not found. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("code", code);
            body.put("userId", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                JOIN_GROUP_URL,
                body,
                response -> {
                    Toast.makeText(this, "Joined group successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EnterGroupCodeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    String msg = "Join failed";
                    if (error.networkResponse != null) {
                        msg += " (" + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(request);
    }
}
