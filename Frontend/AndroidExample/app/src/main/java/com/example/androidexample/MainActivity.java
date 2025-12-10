package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity serves as the central host for the app's primary UI.
 * <p>
 * It manages the BottomNavigationView for switching between the main sections of the app:
 * Home, Groups, and Profile. It also handles theme selection based on user preferences
 * and restores the last selected navigation item on configuration changes (e.g., rotation).
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    /** Bottom navigation bar for switching between main fragments. */
    private BottomNavigationView bottomNav;

    /** Tracks the currently selected navigation item. Defaults to Home. */
    private int selectedNavItemId = R.id.nav_home;

    /** Fragments for the main sections of the app. */
    private final HomeFragment homeFragment = new HomeFragment();
    private final GroupFragment groupFragment = new GroupFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply theme based on user preference
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
                setTheme(R.style.ThemeOverlay_App_Light);
                break;
        }

        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        // Restore last selected navigation item if available
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

    /**
     * Saves the current selected navigation item so it can be restored on configuration changes.
     *
     * @param outState Bundle to save instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SELECTED_NAV_ID", selectedNavItemId);
    }

    /**
     * Loads the corresponding fragment based on the selected BottomNavigationView item.
     *
     * @param itemId the ID of the selected navigation item
     */
    private void loadFragmentForMenuItem(int itemId) {
        Fragment selectedFragment = null;

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_groups) {
            selectedFragment = new GroupFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
    }
}
