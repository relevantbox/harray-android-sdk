package io.relevantbox.demo;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.relevantbox.android.RB;
import io.relevantbox.fcmkit.FcmKitPlugin;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        FcmKitPlugin fcmKitPlugin = RB.plugins().get(FcmKitPlugin.class);
        if (fcmKitPlugin.isRBNotification(remoteMessage)) {
            fcmKitPlugin.handlePushNotification(this, remoteMessage);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        RB.plugins().get(FcmKitPlugin.class).savePushToken(s);
    }
}
