package com.example.androidexample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RecentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent); // This assumes activity_recent.xml exists
    }
}
