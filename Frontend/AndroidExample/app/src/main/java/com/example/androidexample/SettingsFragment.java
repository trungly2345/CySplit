package com.example.androidexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * SettingsFragment provides a user interface for modifying application settings.
 * <p>
 * It uses a PreferenceFragmentCompat to display settings defined in XML
 * and handles theme changes as well as logout functionality.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    /**
     * Initializes the preferences screen and sets up listeners for theme changes
     * and logout actions.
     *
     * @param savedInstanceState Bundle containing saved state of the fragment.
     * @param rootKey            Optional key to display a specific PreferenceScreen.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // Theme preference listener
        ListPreference themePref = findPreference("theme_preference");
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                applyTheme(newValue.toString());
                return true;
            });
        }

        // Logout preference listener
        Preference logoutPref = findPreference("logout_preference");
        if (logoutPref != null) {
            logoutPref.setOnPreferenceClickListener(preference -> {
                performLogout();
                return true;
            });
        }
    }

    /**
     * Applies the selected theme to the application and refreshes the activity.
     *
     * @param themeValue The new theme value ("light", "dark", or "system").
     */
    private void applyTheme(String themeValue) {
        // Update the app theme instantly
        switch (themeValue) {
            case "light":
                requireActivity().setTheme(R.style.ThemeOverlay_App_Light);
                break;
            case "dark":
                requireActivity().setTheme(R.style.ThemeOverlay_App_Dark);
                break;
            case "system":
            default:
                requireActivity().setTheme(R.style.ThemeOverlay_App_Light);
                break;
        }

        // Refresh activity
        requireActivity().recreate();
    }

    /**
     * Performs logout by clearing stored user session data and navigating
     * back to the login activity.
     */
    private void performLogout() {
        // Example: clear user session (depends on how you store login)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        prefs.edit().clear().apply();

        // Navigate back to login activity
        // Replace "LoginActivity.class" with your actual login screen
        startActivity(new android.content.Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }
}