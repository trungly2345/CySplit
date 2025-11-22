package com.example.androidexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Singleton class that manages a single instance of {@link RequestQueue} and {@link ImageLoader}
 * for the application using the Volley library.
 * <p>
 * Provides thread-safe access to network requests and image caching.
 */
public class VolleySingleton {

    /** Singleton instance of VolleySingleton. */
    private static volatile VolleySingleton instance;

    /** RequestQueue for network requests. */
    private final RequestQueue requestQueue;

    /** ImageLoader for efficient image loading and caching. */
    private final ImageLoader imageLoader;

    /**
     * Private constructor to initialize the RequestQueue and ImageLoader.
     *
     * @param context Application context.
     */
    private VolleySingleton(Context context) {
        Context appContext = context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(appContext);

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(25);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    /**
     * Returns the singleton instance of {@link VolleySingleton}, creating it if necessary.
     * <p>
     * Uses double-checked locking for thread-safe initialization.
     *
     * @param context Application context.
     * @return The VolleySingleton instance.
     */
    public static VolleySingleton getInstance(Context context) {
        if (instance == null) {
            synchronized (VolleySingleton.class) {
                if (instance == null) {
                    instance = new VolleySingleton(context);
                }
            }
        }
        return instance;
    }

    /**
     * Returns the {@link RequestQueue} for sending network requests.
     *
     * @return The RequestQueue instance.
     */
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * Adds a request to the {@link RequestQueue}.
     *
     * @param request The request to add.
     * @param <T>     The type of response expected.
     */
    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }

    /**
     * Returns the {@link ImageLoader} for loading and caching images efficiently.
     *
     * @return The ImageLoader instance.
     */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}