package com.example.androidexample;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

public class BillActivity extends AppCompatActivity {

    private TextView restaurantNameText;
    private TextView totalAmountText;
    private TextView itemsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        restaurantNameText = findViewById(R.id.restaurant_name);
        totalAmountText = findViewById(R.id.total_amount);
        itemsText = findViewById(R.id.items_list);

        String restaurantName = getIntent().getStringExtra("restaurantName");
        double totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        String itemsJson = getIntent().getStringExtra("itemsJson");

        restaurantNameText.setText(restaurantName);
        totalAmountText.setText("$" + totalAmount);

        try {
            JSONArray itemsArray = new JSONArray(itemsJson);
            StringBuilder list = new StringBuilder();

            for (int i = 0; i < itemsArray.length(); i++) {
                String name = itemsArray.getJSONObject(i).getString("name");
                double price = itemsArray.getJSONObject(i).getDouble("price");

                list.append(name).append(" - $").append(price).append("\n");
            }

            itemsText.setText(list.toString());

        } catch (JSONException e) {
            itemsText.setText("Error loading items.");
        }
    }
}
