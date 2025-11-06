package com.example.androidexample;

import com.example.androidexample.Bill;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BillService {
    @GET("bill/{bill_id}")
    Call<Bill> getBillById(@Path("bill_id") int billId);
}