//package com.example.androidexample;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.yourapp.api.ApiClient;
//import com.yourapp.api.UserService;
//import com.yourapp.models.UpdateUserRequest;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class EditPaymentActivity extends AppCompatActivity {
//
//    private EditText editPaymentMethod;
//    private Button btnSavePayment;
//
//    private UserService userService;
//    private SharedPreferences prefs;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_payment);
//
//        editPaymentMethod = findViewById(R.id.editPaymentMethod);
//        btnSavePayment = findViewById(R.id.btnSavePayment);
//
//        userService = ApiClient.getClient().create(UserService.class);
//        prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
//
//        loadCurrentPayment();
//
//        btnSavePayment.setOnClickListener(v -> savePayment());
//    }
//
//    private void loadCurrentPayment() {
//        String currentPayment = prefs.getString("paymentMethod", "");
//        editPaymentMethod.setText(currentPayment);
//    }
//
//    private void savePayment() {
//        String newPayment = editPaymentMethod.getText().toString().trim();
//
//        if (TextUtils.isEmpty(newPayment)) {
//            Toast.makeText(this, "Payment method cannot be empty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        int userId = prefs.getInt("userId", -1);
//
//        if (userId == -1) {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        UpdateUserRequest request = new UpdateUserRequest();
//        request.setPaymentMethod(newPayment);
//
//        Call<Void> call = userService.updateUser(userId, request);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//
//                    // Save locally
//                    prefs.edit().putString("paymentMethod", newPayment).apply();
//
//                    Toast.makeText(EditPaymentActivity.this, "Payment updated", Toast.LENGTH_SHORT).show();
//                    finish(); // Close activity and return
//                } else {
//                    Toast.makeText(EditPaymentActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(EditPaymentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
