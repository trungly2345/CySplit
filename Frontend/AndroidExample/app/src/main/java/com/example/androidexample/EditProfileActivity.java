package com.example.androidexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Activity that allows the user to edit their profile information.
 * <p>
 * Users can update their name, email, phone number, password, payment method, and profile picture.
 * Changes are stored locally in SharedPreferences and sent to the server via a PUT request using Volley.
 * Handles image selection from gallery or camera with proper permission handling.
 * </p>
 */
public class EditProfileActivity extends AppCompatActivity {

    /** Request code for camera permission. */
    private static final int CAMERA_PERMISSION_CODE = 100;

    /** Base URL for the user API endpoint. */
    private static final String BASE_URL = "http://coms-3090-039.class.las.iastate.edu:8080/users/";

    /** EditText field for user's name. */
    private EditText nameField;

    /** EditText field for user's email. */
    private EditText emailField;

    /** EditText field for user's phone number. */
    private EditText phoneField;

    /** EditText field for user's password. */
    private EditText passwordField;

    /** EditText field for user's payment method. */
    private EditText paymentField;

    /** Button to save profile changes. */
    private Button saveButton;

    /** Button to change profile picture. */
    private Button changePicButton;

    /** ImageView displaying the user's profile picture. */
    private ImageView profileImageView;

    /** Bitmap representing the currently selected profile picture. */
    private Bitmap selectedImageBitmap;

    /** Volley request queue for network requests. */
    private RequestQueue requestQueue;

    /** ActivityResultLauncher for picking images from gallery. */
    private ActivityResultLauncher<Intent> galleryLauncher;

    /** ActivityResultLauncher for taking pictures with the camera. */
    private ActivityResultLauncher<Intent> cameraLauncher;

    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views and request queue
        requestQueue = Volley.newRequestQueue(this);
        profileImageView = findViewById(R.id.edit_profile_image);
        nameField = findViewById(R.id.edit_name);
        emailField = findViewById(R.id.edit_email);
        phoneField = findViewById(R.id.edit_phone);
        passwordField = findViewById(R.id.edit_password);
        paymentField = findViewById(R.id.edit_payment);
        changePicButton = findViewById(R.id.change_pic_button);
        saveButton = findViewById(R.id.save_button);

        // Load saved profile data from SharedPreferences
        loadLocalProfile();

        // Setup gallery and camera launchers
        setupActivityResultLaunchers();

        // Set button click listeners
        changePicButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

    /**
     * Loads profile data from SharedPreferences and displays it in the UI.
     */
    private void loadLocalProfile() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        nameField.setText(prefs.getString("name", ""));
        emailField.setText(prefs.getString("email", ""));
        phoneField.setText(prefs.getString("phoneNumber", ""));
        paymentField.setText(prefs.getString("paymentMethod", ""));
        passwordField.setText(prefs.getString("password", ""));

        String imageBase64 = prefs.getString("profileImage", null);
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] decoded = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            profileImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Saves the changes made to the profile, updating both local storage and server data.
     */
    private void saveProfileChanges() {
        String newName = nameField.getText().toString().trim();
        String newEmail = emailField.getText().toString().trim();
        String newPhone = phoneField.getText().toString().trim();
        String newPassword = passwordField.getText().toString().trim();
        String newPayment = paymentField.getText().toString().trim();
        updateLocalProfile(newName, newEmail, newPhone, newPassword, newPayment);

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String username = prefs.getString("username", "");

        if (userId == -1 || username.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            updateProfileOnServer(userId, username, newName, newEmail, newPhone, newPayment, newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to build request", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sends a PUT request to update the user's profile on the server.
     *
     * @param userId   the user's ID
     * @param username the user's username
     * @param name     updated name
     * @param email    updated email
     * @param phone    updated phone number
     * @param payment  updated payment method
     * @param password updated password
     * @throws JSONException if JSON construction fails
     */
    private void updateProfileOnServer(int userId, String username, String name, String email,
                                       String phone, String payment, String password) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("userName", username);
        if (!password.isEmpty()) json.put("userPassword", password);
        json.put("name", name);
        json.put("emailId", email);
        json.put("phoneNumber", phone);
        json.put("paymentMethod", payment);
        json.put("userRating", 4.7);
        json.put("ifActive", true);

        String imageBase64 = bitmapToBase64(selectedImageBitmap);
        if (imageBase64 != null) json.put("profileImage", imageBase64);

        String url = BASE_URL + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                json,
                response -> {
                    updateLocalProfile(name, email, phone, password, payment);
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String msg = "Error updating profile";
                    if (error.networkResponse != null) {
                        int code = error.networkResponse.statusCode;
                        if (code == 404) msg = "User not found.";
                        else if (code == 409) msg = "Username already exists.";
                        else msg = "Server error (" + code + ")";
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    /**
     * Sets up ActivityResultLaunchers for handling gallery and camera results.
     */
    private void setupActivityResultLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            profileImageView.setImageBitmap(selectedImageBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        selectedImageBitmap = (Bitmap) extras.get("data");
                        profileImageView.setImageBitmap(selectedImageBitmap);
                    }
                }
        );
    }

    /**
     * Opens a dialog to pick a profile picture from gallery or camera.
     */
    private void openImagePicker() {
        String[] options = {"Gallery", "Camera"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Picture");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraLauncher.launch(cameraIntent);
                }
            }
        });
        builder.show();
    }

    /**
     * Converts a Bitmap to a Base64-encoded string.
     *
     * @param bitmap the Bitmap to convert
     * @return Base64 string or null if bitmap is null
     */
    private String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    /**
     * Updates the local SharedPreferences with profile changes.
     *
     * @param name     updated name
     * @param email    updated email
     * @param phone    updated phone
     * @param password updated password
     * @param payment  updated payment method
     */
    private void updateLocalProfile(String name, String email, String phone, String password, String payment) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phoneNumber", phone);
        editor.putString("password", password);
        editor.putString("paymentMethod", payment);

        if (selectedImageBitmap != null) {
            editor.putString("profileImage", bitmapToBase64(selectedImageBitmap));
        }

        editor.apply();
    }

    /**
     * Handles permission results for the camera.
     *
     * @param requestCode  request code
     * @param permissions  requested permissions
     * @param grantResults grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}