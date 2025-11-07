package com.example.androidexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private ImageView profileImageView;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private Button editProfileButton;
    private ImageView notificationsIcon;

    private BroadcastReceiver profileUpdateReceiver;

    private static final String DEFAULT_PROFILE_IMAGE = "";

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImageView = view.findViewById(R.id.imageView2);
        userNameTextView = view.findViewById(R.id.textView);
        userEmailTextView = view.findViewById(R.id.textView2);
        editProfileButton = view.findViewById(R.id.button2);
        notificationsIcon = view.findViewById(R.id.imageViewNotifications);

        loadProfileData();

        notificationsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NotificationsActivity.class);
            startActivity(intent);
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();

        // Register receiver for profile updates
        if (profileUpdateReceiver == null) {
            profileUpdateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    loadProfileData();
                }
            };
        }
        requireActivity().registerReceiver(profileUpdateReceiver,
                new IntentFilter("com.example.androidexample.PROFILE_UPDATED"));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (profileUpdateReceiver != null) {
            requireActivity().unregisterReceiver(profileUpdateReceiver);
        }
    }

    private void loadProfileData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        String name = prefs.getString("name", "User");
        String email = prefs.getString("email", "user@example.com");
        String profileImageBase64 = prefs.getString("profileImage", null);
        String profileImageUrl = prefs.getString("profileImageUrl", DEFAULT_PROFILE_IMAGE);

        if (userNameTextView != null) userNameTextView.setText(name);
        if (userEmailTextView != null) userEmailTextView.setText(email);

        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(profileImageBase64, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (profileImageView != null && bmp != null) profileImageView.setImageBitmap(bmp);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            ImageRequest imageRequest = new ImageRequest(
                    profileImageUrl,
                    bitmap -> {
                        if (profileImageView != null) profileImageView.setImageBitmap(bitmap);
                    },
                    0, 0,
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.ARGB_8888,
                    error -> {
                        if (profileImageView != null) profileImageView.setImageResource(R.drawable.baseline_person_24);
                    }
            );
            Volley.newRequestQueue(requireContext()).add(imageRequest);
        } else {
            if (profileImageView != null) profileImageView.setImageResource(R.drawable.baseline_person_24);
        }
    }
}
