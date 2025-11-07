package com.example.androidexample;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Intent;

import com.android.volley.toolbox.ImageRequest;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneNumber;
    private Button editProfileButton;
    private ImageView settingsIcon;
    private ImageView notificationsIcon;

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
        userPhoneNumber = view.findViewById(R.id.textView7);
        editProfileButton = view.findViewById(R.id.button2);

        notificationsIcon = view.findViewById(R.id.imageViewNotifications);
        settingsIcon = view.findViewById(R.id.imageViewSettings);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
        String email = prefs.getString("email", "user@example.com");
        String name = prefs.getString("name", "User");
        String phoneNumber = prefs.getString("phone", "");

        userNameTextView.setText(name);
        userEmailTextView.setText(email);
        userPhoneNumber.setText(phoneNumber);


        String profileImageUrl = prefs.getString("profileImageUrl", DEFAULT_PROFILE_IMAGE);
        if (!profileImageUrl.isEmpty()) {
            ImageRequest imageRequest = new ImageRequest(
                    profileImageUrl,
                    bitmap -> profileImageView.setImageBitmap(bitmap),
                    0, 0,
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.ARGB_8888,
                    error -> profileImageView.setImageResource(R.drawable.baseline_person_24)
            );
            VolleySingleton.getInstance(requireContext()).addToRequestQueue(imageRequest);
        } else {
            profileImageView.setImageResource(R.drawable.baseline_person_24);
        }

        notificationsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NotificationsActivity.class);
            startActivity(intent);
        });

        settingsIcon.setOnClickListener(v -> {
            try {
                SettingsFragment fragment = new SettingsFragment();

                Bundle bundle = new Bundle();
                fragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

    }
}
