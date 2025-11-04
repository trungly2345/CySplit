package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private int selectedNavItemId = R.id.nav_home; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = prefs.getString("theme_preference", "system");

        switch (theme) {
            case "light":
                setTheme(R.style.ThemeOverlay_App_Light);
                break;
            case "dark":
                setTheme(R.style.ThemeOverlay_App_Dark);
                break;
            case "system":
            default:
                setTheme(R.style.Theme_AndroidExample);
                break;
        }

        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState != null) {
            selectedNavItemId = savedInstanceState.getInt("SELECTED_NAV_ID", R.id.nav_home);
        }

        bottomNav.setSelectedItemId(selectedNavItemId);
        loadFragmentForMenuItem(selectedNavItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            selectedNavItemId = item.getItemId();
            loadFragmentForMenuItem(selectedNavItemId);
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SELECTED_NAV_ID", selectedNavItemId);
    }

    private void loadFragmentForMenuItem(int itemId) {
        Fragment selectedFragment = null;

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_groups) {
            selectedFragment = new GroupFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (itemId == R.id.nav_login) {
            selectedFragment = new LoginFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
    }
}
