package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity for handling user login.
 * <p>
 * Provides UI for entering username and password, sending login requests to the backend,
 * and managing user session data via SharedPreferences. Also includes a redirect to
 * the signup screen and a simple "forgot password" placeholder.
 * </p>
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPassword, signUpRedirect;

    private RequestQueue requestQueue;

    /** Endpoint URL for login requests. */
    private final String LOGIN_URL = "http://coms-3090-039.class.las.iastate.edu:8080/users/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(this);

        // Initialize UI elements
        usernameEditText = findViewById(R.id.login_username);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.forgot_password);
        signUpRedirect = findViewById(R.id.signUpRedirectText);

        // Login button click handler
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hardcoded login for testing
            if (username.equals("hi") && password.equals("hi")) {
                saveSession(1, "hi", "hi@example.com");
                Toast.makeText(LoginActivity.this, "Welcome, hi!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                return;
            }

            // Send request to server
            sendLoginRequest(username, password);
        });

        // Redirect to signup activity
        signUpRedirect.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class))
        );
    }

    /**
     * Sends a login request to the backend server using Volley.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     */
    private void sendLoginRequest(String username, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("userName", username);
            json.put("userPassword", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_URL,
                json,
                response -> {
                    try {
                        int userId = response.optInt("id", -1);
                        String userName = response.optString("userName");
                        String email = response.optString("emailId");

                        if (userId == -1) {
                            Toast.makeText(LoginActivity.this, "Invalid server response: no ID", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        saveSession(userId, userName, email);

                        Toast.makeText(LoginActivity.this, "Welcome, " + userName + "!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Response parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMsg = "Login failed";
                    if (error.networkResponse != null) {
                        errorMsg += " (" + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Saves user session data to SharedPreferences.
     *
     * @param userId   the unique ID of the user
     * @param username the username
     * @param email    the user's email
     */
    private void saveSession(int userId, String username, String email) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putInt("user_id", userId);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.apply();
    }
}