package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText, emailEditText, passwordEditText;
    private TextInputEditText nameEditText, phoneEditText, paymentEditText;
    private MaterialButton signupButton;
    private RequestQueue requestQueue;

    private final String SIGNUP_URL = "http://coms-3090-039.class.las.iastate.edu:8080/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        requestQueue = Volley.newRequestQueue(this);

        usernameEditText = findViewById(R.id.signup_username);
        emailEditText = findViewById(R.id.signup_email);
        passwordEditText = findViewById(R.id.signup_password);
        nameEditText = findViewById(R.id.signup_name);
        phoneEditText = findViewById(R.id.signup_phone);
        paymentEditText = findViewById(R.id.signup_payment);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String payment = paymentEditText.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() ||
                    name.isEmpty() || phone.isEmpty() || payment.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                sendSignupRequest(username, email, password, name, phone, payment);
            }
        });

        TextView loginRedirectText = findViewById(R.id.loginRedirectText);
        loginRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void sendSignupRequest(String username, String email, String password,
                                   String name, String phone, String payment) {
        JSONObject json = new JSONObject();
        try {
            json.put("userName", username);
            json.put("userPassword", password);
            json.put("emailId", email);
            json.put("ifActive", true);
            json.put("name", name);
            json.put("phoneNumber", phone);
            json.put("paymentMethod", payment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                SIGNUP_URL,
                json,
                response -> {
                    Toast.makeText(SignupActivity.this, "Signup successful! Please login.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();
                },
                error -> {
                    String errorMsg = error.getMessage();
                    if (error.networkResponse != null) {
                        errorMsg = "Error " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(SignupActivity.this, "Signup failed: " + errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
