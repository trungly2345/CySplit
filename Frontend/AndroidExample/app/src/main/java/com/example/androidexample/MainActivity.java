package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;

    private Button profileButton;

    private Button logout;
    private TextView userName;


    private BottomNavigationView bottomNav;
    private HomeFragment homeFragment = new HomeFragment();
    private GroupFragment groupFragment = new GroupFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageText = findViewById(R.id.main_msg_txt);
        profileButton = findViewById(R.id.profile_btn);
        userName = findViewById(R.id.userName);
        logout = findViewById(R.id.logout);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        Bundle extras = getIntent().getExtras();

        if (isLoggedIn && extras != null) {
            String email = prefs.getString("email", "User");
            userName.setText(email);
        } else {
            // If not logged in, go back to LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.nav_login);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadFragment(homeFragment);
            } else if (itemId == R.id.nav_groups) {
                loadFragment(groupFragment);
            } else if (itemId == R.id.nav_profile) {
                loadFragment(profileFragment);
            }

            return true;
        });

        profileButton.setOnClickListener(v -> {
            bottomNav.setSelectedItemId(R.id.nav_profile);
            loadFragment(profileFragment);
        });
    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
