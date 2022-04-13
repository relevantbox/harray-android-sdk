package io.relevantbox.demo;

import android.app.Application;

import io.relevantbox.android.RB;
import io.relevantbox.android.common.RBConfig;
import io.relevantbox.hmskit.HmsKitPlugin;

public class RBHuaweiDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RBConfig rbConfig =
                RBConfig
                        .init("RB-XMjJ4RzzvbPc0T2")
                        .useRBPlugin(HmsKitPlugin.class);
        RB.configure(this, rbConfig);
    }
}
