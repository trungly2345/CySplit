package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GroupLobbyActivity extends AppCompatActivity {

    private TextView titleText, membersText, waitingText;
    private ProgressBar loading;
    private Button openBillsBtn, refreshBtn;

    private int groupId;
    private String groupName;

    private static final String BASE_URL = "http://coms-3090-039.class.las.iastate.edu:8080";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lobby);

        titleText = findViewById(R.id.group_lobby_title);
        membersText = findViewById(R.id.group_members_text);
        waitingText = findViewById(R.id.waiting_for_bill_text);
        loading = findViewById(R.id.group_lobby_spinner);
        openBillsBtn = findViewById(R.id.group_open_bills_button);

        refreshBtn = findViewById(R.id.group_refresh_button);

        groupId = getIntent().getIntExtra("groupId", -1);
        groupName = getIntent().getStringExtra("groupName");

        if (groupId == -1) {
            Toast.makeText(this, "Missing group info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        titleText.setText(groupName);

        loadGroupDetails();

        refreshBtn.setOnClickListener(v -> loadGroupDetails());

        openBillsBtn.setOnClickListener(v -> {
            Intent i = new Intent(GroupLobbyActivity.this, BillItemsActivity.class);
            i.putExtra("groupId", groupId);
            i.putExtra("groupName", groupName);
            startActivity(i);
        });
    }

    private void loadGroupDetails() {
        loading.setVisibility(View.VISIBLE);
        membersText.setText("");
        waitingText.setText("");

        Request request = new Request.Builder()
                .url(BASE_URL + "/groups/" + groupId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(GroupLobbyActivity.this, "Failed to load group", Toast.LENGTH_SHORT).show();
                });
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> loading.setVisibility(View.GONE));

                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(GroupLobbyActivity.this, "Group not found", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                try {
                    String body = response.body().string();
                    JSONObject groupJson = new JSONObject(body);

                    int members = groupJson.optInt("currentMembers", 0);
                    String groupNameFromServer = groupJson.optString("group_name", groupName);

                    runOnUiThread(() -> {
                        groupName = groupNameFromServer;
                        titleText.setText(groupName);

                        membersText.setText("Members joined: " + members);
                        waitingText.setText("Waiting for bill to be created...");
                    });

                } catch (JSONException e) {
                    runOnUiThread(() ->
                            Toast.makeText(GroupLobbyActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
