package com.example.androidexample;

import com.example.androidexample.Bill;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit service interface for fetching bill data from a backend API.
 * <p>
 * Provides endpoints for retrieving a single bill by ID and retrieving all bills.
 * </p>
 */
public interface BillService {

    /**
     * Fetches a bill by its unique ID.
     *
     * @param billId The ID of the bill to retrieve
     * @return A {@link Call} object for the network request that returns a {@link Bill}
     */
    @GET("bill/{bill_id}")
    Call<Bill> getBillById(@Path("bill_id") int billId);

    /**
     * Fetches all bills from the backend.
     *
     * @return A {@link Call} object for the network request that returns a list of {@link Bill} objects
     */
    @GET("/bill")
    Call<List<Bill>> getAllBills();
}
