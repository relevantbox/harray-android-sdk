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
                        .init("RB-XMjJ4RzzvbPc0T2")
                        .useRBPlugin(FcmKitPlugin.class).inAppNotificationLinkClickHandler(
                        new LinkClickHandler() {
                            @Override
                            public void handle(String link) {

                            }
                        }
                );
        RB.configure(this, rbConfig);
    }
}
