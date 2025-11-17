package com.example.androidexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

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