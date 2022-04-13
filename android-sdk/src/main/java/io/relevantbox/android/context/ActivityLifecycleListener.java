package io.relevantbox.android.context;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.Nullable;

import io.relevantbox.android.utils.RBLogger;

public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    @Nullable
    private static Activity currentActivity;

    @Nullable
    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        RBLogger.log("onActivityCreated:" + activity.getLocalClassName());
        currentActivity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        RBLogger.log("onActivityStarted:" + activity.getLocalClassName());
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        RBLogger.log("onActivityResumed:" + activity.getLocalClassName());
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        RBLogger.log("onActivityPaused:" + activity.getLocalClassName());
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        RBLogger.log("onActivityStopped:" + activity.getLocalClassName());
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        RBLogger.log("onActivityDestroyed:" + activity.getLocalClassName());
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }
}
