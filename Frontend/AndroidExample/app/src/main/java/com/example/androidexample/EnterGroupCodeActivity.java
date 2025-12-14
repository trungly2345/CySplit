package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EnterGroupCodeActivity extends AppCompatActivity {

    private EditText codeInput;
    private Button joinButton;

    private final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://coms-3090-039.class.las.iastate.edu:8080";

    private int userId = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_group_code);

        codeInput = findViewById(R.id.group_code_input);
        joinButton = findViewById(R.id.join_group_button);

        joinButton.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim().toUpperCase();
            if (code.isEmpty()) {
                Toast.makeText(this, "Please enter a code", Toast.LENGTH_SHORT).show();
                return;
            }
            validateGroupCode(code);
        });
    }

    private void validateGroupCode(String code) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/groups/code/" + code)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(EnterGroupCodeActivity.this,
                                "Network error", Toast.LENGTH_SHORT).show()
                );
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 404) {
                    runOnUiThread(() ->
                            Toast.makeText(EnterGroupCodeActivity.this,
                                    "Invalid or expired code", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(EnterGroupCodeActivity.this,
                                    "Error validating code", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String body = response.body().string();
                Log.e("JOIN_CODE_RAW_RESPONSE", body);
                Log.e("JOIN_CODE_HTTP", String.valueOf(response.code()));

                int groupId;
                String groupName;

                try {
                    JSONObject groupJson = new JSONObject(body);

                    groupId = groupJson.getInt("id");
                    groupName = groupJson.getString("group_name");

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(
                                    EnterGroupCodeActivity.this,
                                    "Invalid server response",
                                    Toast.LENGTH_SHORT
                            ).show()
                    );
                    return;
                }

                joinGroup(code, groupId, groupName);

            }
        });
    }

    private void joinGroup(String code, int groupId, String groupName) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/groups/join/" + code + "?userId=" + userId)
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(EnterGroupCodeActivity.this,
                                "Join error", Toast.LENGTH_SHORT).show()
                );
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(EnterGroupCodeActivity.this,
                                    "Failed to join group", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                runOnUiThread(() -> {
                    Intent i = new Intent(EnterGroupCodeActivity.this, GroupLobbyActivity.class);
                    i.putExtra("groupId", groupId);
                    i.putExtra("groupName", groupName);
                    startActivity(i);
                    finish();
                });
            }
        });
    }
}
