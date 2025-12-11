package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class GroupLobbyActivity extends AppCompatActivity {

    private TextView titleText, membersText, waitingText;
    private ProgressBar loading;
    private Button openBillsBtn;

    private int groupId = 87;
    private String groupName = "Table 7 - Dec 10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lobby);

        titleText = findViewById(R.id.group_lobby_title);
        membersText = findViewById(R.id.group_members_text);
        waitingText = findViewById(R.id.waiting_for_bill_text);
        loading = findViewById(R.id.group_lobby_spinner);
        openBillsBtn = findViewById(R.id.group_open_bills_button);

        if (getIntent().hasExtra("groupId")) {
            groupId = getIntent().getIntExtra("groupId", groupId);
        }
        if (getIntent().hasExtra("groupName")) {
            groupName = getIntent().getStringExtra("groupName");
        }

        titleText.setText(groupName);
        membersText.setText("Members joined: 3");
        waitingText.setText("Waiting for bill to be created...");

        openBillsBtn.setOnClickListener(v -> {
            Intent i = new Intent(GroupLobbyActivity.this, BillItemsActivity.class);
            i.putExtra("groupId", groupId);
            i.putExtra("groupName", groupName);
            startActivity(i);
        });
    }
}
