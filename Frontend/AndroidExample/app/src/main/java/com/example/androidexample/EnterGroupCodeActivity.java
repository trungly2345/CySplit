package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EnterGroupCodeActivity extends AppCompatActivity {

    private EditText codeInput;
    private Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_group_code);

        codeInput = findViewById(R.id.group_code_input);
        joinButton = findViewById(R.id.join_group_button);

        joinButton.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Please enter a code", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(EnterGroupCodeActivity.this, GroupLobbyActivity.class);
            i.putExtra("groupName", "Group " + code.toUpperCase());
            i.putExtra("groupId", 999);
            startActivity(i);
            finish();
        });
    }
}
