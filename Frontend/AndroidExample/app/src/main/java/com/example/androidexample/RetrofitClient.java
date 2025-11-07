package com.example.androidexample;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit localRetrofit = null;
    private static Retrofit remoteRetrofit = null;

    // For bills (local backend)
    public static Retrofit getLocalClient() {
        if (localRetrofit == null) {
            localRetrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/") // local backend
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return localRetrofit;
    }

    // For groups (live server)
    public static Retrofit getRemoteClient() {
        if (remoteRetrofit == null) {
            remoteRetrofit = new Retrofit.Builder()
                    .baseUrl("http://coms-3090-039.class.las.iastate.edu:8080/") // live server
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return remoteRetrofit;
    }
}