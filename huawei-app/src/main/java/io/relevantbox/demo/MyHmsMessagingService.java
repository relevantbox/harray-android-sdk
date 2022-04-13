package io.relevantbox.demo;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import io.relevantbox.android.RB;
import io.relevantbox.hmskit.HmsKitPlugin;

public class MyHmsMessagingService extends HmsMessageService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        HmsKitPlugin hmsKitPlugin = RB.plugins().get(HmsKitPlugin.class);
        if(hmsKitPlugin.isRBNotification(remoteMessage)){
            hmsKitPlugin.handlePushNotification(this, remoteMessage);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        RB.plugins().get(HmsKitPlugin.class).savePushToken(s);
    }
}