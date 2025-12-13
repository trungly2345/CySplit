package com.example.androidexample;

import com.google.gson.annotations.SerializedName;

public class BillItemDto {
    @SerializedName("itemId")
    public int itemId;

    @SerializedName("itemName")
    public String itemName;

    @SerializedName("itemPrice")
    public String itemPrice;

    @SerializedName("quantity")
    public int quantity;

    @SerializedName("paid")
    public boolean paid;
}
