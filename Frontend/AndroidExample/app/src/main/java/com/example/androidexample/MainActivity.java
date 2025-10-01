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

<<<<<<< HEAD
    private TextView messageText;
    private TextView usernameText;
    private Button loginButton;
    private Button signupButton;
    private Button profileButton;
    private Button logoutButton;
=======
>>>>>>> 004ae3e146ef8ce605b7de76e746551b80e13974
    private BottomNavigationView bottomNav;
    private HomeFragment homeFragment = new HomeFragment();
    private GroupFragment groupFragment = new GroupFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private LoginFragment loginFragment = new LoginFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        messageText = findViewById(R.id.main_msg_txt);
        usernameText = findViewById(R.id.main_username_txt);
        loginButton = findViewById(R.id.main_login_btn);
        signupButton = findViewById(R.id.main_signup_btn);
        profileButton = findViewById(R.id.profile_btn);
        logoutButton = findViewById(R.id.main_logout_btn);
=======
>>>>>>> 004ae3e146ef8ce605b7de76e746551b80e13974
        bottomNav = findViewById(R.id.bottom_navigation);

        loadFragment(homeFragment);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadFragment(homeFragment);
            } else if (itemId == R.id.nav_groups) {
                loadFragment(groupFragment);
            } else if (itemId == R.id.nav_profile) {
                loadFragment(profileFragment);
            }
            else if (itemId == R.id.nav_login){
                loadFragment(loginFragment);
            }

            return true;
        });

<<<<<<< HEAD
        // login
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        Bundle extras = getIntent().getExtras();

        if (isLoggedIn && extras != null) {
            String username = extras.getString("USERNAME");
            messageText.setText("Welcome, " + username + "!");
            usernameText.setText(username);
            usernameText.setVisibility(View.VISIBLE);

            loginButton.setVisibility(View.GONE);
            signupButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            messageText.setText("Home Page");
            usernameText.setVisibility(View.INVISIBLE);

            loginButton.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false); // clear login state
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // login button
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // signup button
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // profile button
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
=======
        Bundle extras = getIntent().getExtras();
>>>>>>> 004ae3e146ef8ce605b7de76e746551b80e13974
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
