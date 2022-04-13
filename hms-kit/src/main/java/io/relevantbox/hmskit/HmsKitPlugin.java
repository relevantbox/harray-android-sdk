package io.relevantbox.hmskit;

import android.content.Context;
import android.content.Intent;

import com.huawei.hms.push.RemoteMessage;

import io.relevantbox.android.context.RBPlugin;
import io.relevantbox.hmskit.common.Constants;
import io.relevantbox.hmskit.notification.NotificationProcessorHandler;

import static io.relevantbox.android.RB.getApplicationContextHolder;
import static io.relevantbox.android.RB.getDeviceService;
import static io.relevantbox.android.RB.getEntitySerializerService;
import static io.relevantbox.android.RB.getHttpService;
import static io.relevantbox.android.RB.getSessionContextHolder;

public class HmsKitPlugin extends RBPlugin {

    private NotificationProcessorHandler notificationProcessorHandler;
    private String pushNotificationToken = "";

    public HmsKitPlugin() {
        this.notificationProcessorHandler = new NotificationProcessorHandler(
                getApplicationContextHolder(),
                getSessionContextHolder(),
                getHttpService(),
                getEntitySerializerService(),
                getDeviceService()
        );
    }

    @Override
    public void onCreate(Context context) {
        resetBadgeCounts(context);
    }

    @Override
    public void onLogin() {
        if (!"".equals(pushNotificationToken)) {
            this.savePushToken(pushNotificationToken);
        }
    }

    @Override
    public void onLogout() {
        removeTokenAssociation(this.pushNotificationToken);
    }

    public void savePushToken(String deviceToken) {
        this.pushNotificationToken = deviceToken;
        notificationProcessorHandler.savePushToken(deviceToken);
    }

    public void removeTokenAssociation(String deviceToken) {
        this.pushNotificationToken = "";
        notificationProcessorHandler.removeTokenAssociation(deviceToken);
    }

    public boolean isRBNotification(RemoteMessage remoteMessage) {
        return Constants.PUSH_CHANNEL_ID.equals(remoteMessage.getDataOfMap().get(Constants.PUSH_PAYLOAD_SOURCE));
    }

    public void handlePushNotification(Context applicationContext, RemoteMessage remoteMessage) {
        notificationProcessorHandler.handlePushNotification(applicationContext, remoteMessage);
    }

    public void pushMessageOpened(Intent intent) {
        notificationProcessorHandler.pushMessageOpened(intent);
    }

    public void resetBadgeCounts(Context applicationContext) {
        notificationProcessorHandler.resetBadgeCounts(applicationContext);
    }
}
