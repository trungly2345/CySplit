package com.example.androidexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView, notificationsIcon, settingsIcon, paymentsIcon;
    private TextView userNameTextView, userEmailTextView, userPhoneNumber;
    private Button editProfileButton;

    private BroadcastReceiver profileUpdateReceiver;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "UserPrefs";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        bindViews(view);
        loadProfileData();
        setupListeners();
    }

    private void bindViews(View view) {
        profileImageView = view.findViewById(R.id.imageView2);
        userNameTextView = view.findViewById(R.id.textView);
        userEmailTextView = view.findViewById(R.id.textView2);
        userPhoneNumber = view.findViewById(R.id.textViewMobileValue);

        notificationsIcon = view.findViewById(R.id.imageViewNotifications);
        settingsIcon = view.findViewById(R.id.imageViewSettings);
        paymentsIcon = view.findViewById(R.id.imageViewPayments);

        editProfileButton = view.findViewById(R.id.button2);
    }

    private void setupListeners() {
        notificationsIcon.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), NotificationsActivity.class)));

        settingsIcon.setOnClickListener(v -> {
            SettingsFragment fragment = new SettingsFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        paymentsIcon.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), PaymentsActivity.class)));

        editProfileButton.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), EditProfileActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
        registerProfileReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterProfileReceiver();
    }

    private void registerProfileReceiver() {
        if (profileUpdateReceiver == null) {
            profileUpdateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    loadProfileData();
                }
            };
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(
                    profileUpdateReceiver,
                    new IntentFilter("com.example.androidexample.PROFILE_UPDATED"),
                    Context.RECEIVER_NOT_EXPORTED
            );
        }
    }

    private void unregisterProfileReceiver() {
        try {
            requireActivity().unregisterReceiver(profileUpdateReceiver);
        } catch (IllegalArgumentException ignored) {}
    }

    private void loadProfileData() {
        String name = prefs.getString("name", "User");
        String email = prefs.getString("email", "user@example.com");
        String phone = prefs.getString("phoneNumber", "N/A");
        String profileImageBase64 = prefs.getString("profileImage", null);

        userNameTextView.setText(name);
        userEmailTextView.setText(email);
        userPhoneNumber.setText(phone);

        loadProfileImage(profileImageBase64);
    }

    private void loadProfileImage(String base64) {
        if (base64 != null && !base64.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                if (bmp != null) {
                    profileImageView.setImageBitmap(bmp);
                    return;
                }
            } catch (Exception ignored) {}
        }

        profileImageView.setImageResource(R.drawable.baseline_person_24);
    }
}

