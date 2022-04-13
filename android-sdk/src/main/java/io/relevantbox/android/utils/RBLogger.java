package io.relevantbox.android.utils;

import android.util.Log;

import io.relevantbox.android.common.Constants;

public class RBLogger {
    public static void log(String message) {
        Log.d(Constants.LOG_TAG, message);
    }

    public static void log(String message, Throwable throwable) {
        Log.d(Constants.LOG_TAG, message, throwable);
    }
}
