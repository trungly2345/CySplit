package com.example.androidexample;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient is a singleton utility class that provides configured Retrofit
 * instances for communicating with local and remote backends.
 * <p>
 * The local client is intended for testing with a local backend server, while
 * the remote client connects to the live server for production use.
 */
public class RetrofitClient {

    /** Singleton instance of Retrofit for the local backend. */
    private static Retrofit localRetrofit = null;

    /** Singleton instance of Retrofit for the remote/live backend. */
    private static Retrofit remoteRetrofit = null;

    /**
     * Returns a singleton Retrofit instance configured for the local backend.
     * <p>
     * This client is typically used for bills or testing against a local server
     * running on the emulator (10.0.2.2:8080).
     *
     * @return Retrofit instance for the local backend.
     */
    public static Retrofit getLocalClient() {
        if (localRetrofit == null) {
            localRetrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/") // local backend
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return localRetrofit;
    }

    /**
     * Returns a singleton Retrofit instance configured for the remote/live backend.
     * <p>
     * This client is typically used for groups or production server interactions.
     *
     * @return Retrofit instance for the remote backend.
     */
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