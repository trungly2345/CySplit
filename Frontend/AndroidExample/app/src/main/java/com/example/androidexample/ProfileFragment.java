package com.example.androidexample;

import android.annotation.SuppressLint;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

/**
 * ProfileFragment is responsible for displaying the user's profile information,
 * including profile picture, name, email, and phone number.
 * <p>
 * It provides options to edit the profile, navigate to settings, and view notifications.
 * The fragment listens for updates via a BroadcastReceiver and refreshes the displayed data accordingly.
 */
public class ProfileFragment extends Fragment {

    /** ImageView for displaying the user's profile picture. */
    private ImageView profileImageView, notificationsIcon, settingsIcon;

    /** TextViews for displaying the user's name, email, and phone number. */
    private TextView userNameTextView, userEmailTextView, userPhoneNumber;

    /** Button to edit the user's profile. */
    private Button editProfileButton;

    /** Receiver to listen for profile updates. */
    private BroadcastReceiver profileUpdateReceiver;

    /** Default image used if no profile image is available. */
    private static final String DEFAULT_PROFILE_IMAGE = "";

    /** Default constructor. */
    public ProfileFragment() {}

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           LayoutInflater to inflate views in the fragment.
     * @param container          The parent ViewGroup that the fragment's UI should attach to.
     * @param savedInstanceState Bundle containing saved state of the fragment.
     * @return The root view of the fragment layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Uncommented layout click listener example for future payment edit feature
//        LinearLayout paymentsLayout = view.findViewById(R.id.editPayment);
//
//        paymentsLayout.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), com.example.EditPaymentActivity.class);
//            startActivity(intent);
//        });

        return view;
    }

    /**
     * Called immediately after onCreateView. Initializes UI elements and sets up
     * listeners for buttons and icons.
     *
     * @param view               The View returned by onCreateView.
     * @param savedInstanceState Bundle containing saved state of the fragment.
     */
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

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        userNameTextView.setText(prefs.getString("name", "User"));
        userEmailTextView.setText(prefs.getString("email", "user@example.com"));
        userPhoneNumber.setText(prefs.getString("phone", ""));

        loadProfileData();

        notificationsIcon.setOnClickListener(v -> startActivity(
                new Intent(requireActivity(), NotificationsActivity.class)));

        settingsIcon.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .addToBackStack(null)
                .commit());

        editProfileButton.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), EditProfileActivity.class)));
    }

    /**
     * Called when the fragment becomes visible. Registers the BroadcastReceiver
     * to listen for profile updates and reloads profile data.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();

        if (profileUpdateReceiver == null) {
            profileUpdateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    loadProfileData();
                }
            };
        }

        ContextCompat.registerReceiver(
                requireActivity(),
                profileUpdateReceiver,
                new IntentFilter("com.example.androidexample.PROFILE_UPDATED"),
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    /**
     * Called when the fragment is no longer visible. Unregisters the
     * BroadcastReceiver to prevent memory leaks.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (profileUpdateReceiver != null) {
            requireActivity().unregisterReceiver(profileUpdateReceiver);
        }
    }

    /**
     * Loads the user's profile data from SharedPreferences, including name, email,
     * and profile image (base64 string or URL). If no image is available, a default
     * placeholder is used.
     */
    private void loadProfileData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        userNameTextView.setText(prefs.getString("name", "User"));
        userEmailTextView.setText(prefs.getString("email", "user@example.com"));

        String base64Image = prefs.getString("profileImage", null);
        String imageUrl = prefs.getString("profileImageUrl", DEFAULT_PROFILE_IMAGE);

        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bmp != null) {
                    profileImageView.setImageBitmap(bmp);
                    return;
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageRequest request = new ImageRequest(
                    imageUrl,
                    bitmap -> profileImageView.setImageBitmap(bitmap),
                    0, 0,
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.ARGB_8888,
                    error -> profileImageView.setImageResource(R.drawable.baseline_person_24)
            );
            Volley.newRequestQueue(requireContext()).add(request);
        } else {
            profileImageView.setImageResource(R.drawable.baseline_person_24);
        }
    }
}
