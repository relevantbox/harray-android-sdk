package io.relevantbox.demo;

import android.app.Application;

import io.relevantbox.android.RB;
import io.relevantbox.android.common.RBConfig;
import io.relevantbox.android.event.inappnotification.LinkClickHandler;
import io.relevantbox.fcmkit.FcmKitPlugin;

public class RBDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RBConfig rbConfig =
                RBConfig
                        .init("AND-f39a4f151eab43d5963f5c802bd20284")
                        .useRBPlugin(FcmKitPlugin.class).inAppNotificationLinkClickHandler(new XennInAppCallbackHandler())
                        .apiUrl("https://test-api.relevantbox.io:443")
                        .collectorUrl("https://test-collector.relevantbox.io:443");
        RB.configure(this, rbConfig);
    }
}
