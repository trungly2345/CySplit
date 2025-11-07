package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameField, emailField, phoneField;
    private Button saveButton;
    private ProgressBar progressBar;

    private RequestQueue requestQueue;
    private final String BASE_URL = "http://coms-3090-039.class.las.iastate.edu:8080/users/update/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        requestQueue = Volley.newRequestQueue(this);

        nameField = findViewById(R.id.edit_name);
        emailField = findViewById(R.id.edit_email);
        phoneField = findViewById(R.id.edit_phone);
        saveButton = findViewById(R.id.save_button);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        String name = prefs.getString("name", "");
        String email = prefs.getString("email", "");
        String phone = prefs.getString("phone", "");

        nameField.setText(name);
        emailField.setText(email);
        phoneField.setText(phone);

        saveButton.setOnClickListener(v -> {
            String newName = nameField.getText().toString().trim();
            String newEmail = emailField.getText().toString().trim();
            String newPhone = phoneField.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("hi".equalsIgnoreCase(username)) {
                updateLocalProfile(newName, newEmail, newPhone);
                Toast.makeText(this, "Profile updated locally (offline mode)", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            int userId = prefs.getInt("user_id", -1);
            updateProfileOnServer(userId, newName, newEmail, newPhone);
        });
    }

    private void updateLocalProfile(String name, String email, String phone) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.apply();
    }

    private void updateProfileOnServer(int userId, String name, String email, String phone) {
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("email", email);
            json.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = BASE_URL + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                json,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    updateLocalProfile(name, email, phone);
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String errorMsg = error.getMessage();
                    if (error.networkResponse != null) {
                        errorMsg = "Error " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(EditProfileActivity.this, "Update failed: " + errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
