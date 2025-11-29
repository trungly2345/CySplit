package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment representing the home screen of the app.
 * <p>
 * Displays the current user's email and provides a logout button to clear session data
 * and return to the login screen.
 * </p>
 */
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // UI references
        TextView userNameText = view.findViewById(R.id.userName);
        Button logoutBtn = view.findViewById(R.id.logout);

        // Load user email from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        String email = prefs.getString("email", "hi@example.com");
        userNameText.setText(email);

        // Update the singleton user session
        UserSession.getInstance().setUsername(email);

        // Logout button listener
        logoutBtn.setOnClickListener(v -> {
            prefs.edit().clear().apply(); // Clear stored preferences
            startActivity(new Intent(getActivity(), LoginActivity.class)); // Navigate to login screen
            requireActivity().finish(); // Close current activity
        });

        return view;
    }
}
