package com.example.androidexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final String BASE_URL =
            "http://coms-3090-039.class.las.iastate.edu:8080/users/";

    private EditText nameField, emailField, phoneField, passwordField, paymentField;
    private ImageView profileImageView;
    private Bitmap selectedImageBitmap;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        requestQueue = Volley.newRequestQueue(this);

        profileImageView = findViewById(R.id.edit_profile_image);
        nameField = findViewById(R.id.edit_name);
        emailField = findViewById(R.id.edit_email);
        phoneField = findViewById(R.id.edit_phone);
        passwordField = findViewById(R.id.edit_password);
        paymentField = findViewById(R.id.edit_payment);

        Button changePicButton = findViewById(R.id.change_pic_button);
        Button saveButton = findViewById(R.id.save_button);

        loadLocalProfile();
        setupActivityResultLaunchers();

        changePicButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

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
            selectedImageBitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            profileImageView.setImageBitmap(selectedImageBitmap);
        }
    }

    private void saveProfileChanges() {
        String newName = nameField.getText().toString().trim();
        String newEmail = emailField.getText().toString().trim();
        String newPhone = phoneField.getText().toString().trim();
        String newPassword = passwordField.getText().toString().trim();
        String newPayment = paymentField.getText().toString().trim();

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
            Toast.makeText(this, "Failed to build request", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfileOnServer(int userId, String username,
                                       String name, String email,
                                       String phone, String payment, String password)
            throws JSONException {

        JSONObject json = new JSONObject();
        json.put("userName", username);
        json.put("name", name);
        json.put("emailId", email);
        json.put("phoneNumber", phone);
        json.put("paymentMethod", payment);
        json.put("userRating", 4.7);
        json.put("ifActive", true);

        if (!password.isEmpty()) {
            json.put("userPassword", password);
        }

        String imageBase64 = bitmapToBase64(selectedImageBitmap);
        if (imageBase64 != null) {
            json.put("profileImage", imageBase64);
        }

        String url = BASE_URL + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT, url, json,
                response -> {
                    saveLocalProfile(name, email, phone, password, payment);
                    broadcastProfileUpdated();
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String msg = "Error updating profile";
                    if (error.networkResponse != null) {
                        int code = error.networkResponse.statusCode;
                        if (code == 404) msg = "User not found";
                        else if (code == 409) msg = "Username already exists";
                        else msg = "Server error (" + code + ")";
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    private void saveLocalProfile(String name, String email, String phone,
                                  String password, String payment) {

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phoneNumber", phone);
        editor.putString("paymentMethod", payment);

        if (!password.isEmpty()) {
            editor.putString("password", password);
        }

        if (selectedImageBitmap != null) {
            editor.putString("profileImage", bitmapToBase64(selectedImageBitmap));
        }

        editor.apply();
    }

    private void broadcastProfileUpdated() {
        Intent intent = new Intent("PROFILE_UPDATED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void setupActivityResultLaunchers() {

        galleryLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try (InputStream stream = getContentResolver().openInputStream(uri)) {
                            selectedImageBitmap = BitmapFactory.decodeStream(stream);
                            profileImageView.setImageBitmap(selectedImageBitmap);
                        } catch (IOException e) {
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        cameraLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap bmp = (Bitmap) result.getData().getExtras().get("data");
                        selectedImageBitmap = bmp;
                        profileImageView.setImageBitmap(bmp);
                    }
                });
    }

    private void openImagePicker() {
        String[] options = {"Gallery", "Camera"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Choose Profile Picture")
                .setItems(options, (d, i) -> {
                    if (i == 0) {
                        Intent galleryIntent =
                                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryLauncher.launch(galleryIntent);
                    } else {
                        requestCamera();
                    }
                }).show();
    }

    private void requestCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            cameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            cameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));

        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private String bitmapToBase64(Bitmap bmp) {
        if (bmp == null) return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
