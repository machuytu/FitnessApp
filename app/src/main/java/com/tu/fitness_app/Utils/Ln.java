package com.tu.fitness_app.Utils;

import android.util.Log;

import com.tu.fitness_app.BuildConfig;

/**
 * Simplify calls to {@link Log} and avoid verbose and debug message to be shown in production builds
 */
public class Ln {
    private static final String TAG = "FoodFacts";

    private Ln() {

    }

    public static void v(String message) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, message);
        }
    }

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void e(String message, Throwable t) {
        Log.e(TAG, message, t);
    }
}
