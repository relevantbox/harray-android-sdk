package io.relevantbox.fcmkit.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.relevantbox.android.context.ApplicationContextHolder;
import io.relevantbox.android.context.SessionContextHolder;
import io.relevantbox.android.model.RBEvent;
import io.relevantbox.android.service.DeviceService;
import io.relevantbox.android.service.EntitySerializerService;
import io.relevantbox.android.service.HttpService;
import io.relevantbox.android.utils.RBLogger;
import io.relevantbox.fcmkit.common.Constants;
import io.relevantbox.fcmkit.common.PushMessageDataWrapper;
import io.relevantbox.fcmkit.model.FeedbackEvent;

public class NotificationProcessorHandler {

    private final ApplicationContextHolder applicationContextHolder;
    private final SessionContextHolder sessionContextHolder;
    private final HttpService httpService;
    private final EntitySerializerService entitySerializerService;
    private final DeviceService deviceService;

    public NotificationProcessorHandler(ApplicationContextHolder applicationContextHolder,
                                        SessionContextHolder sessionContextHolder,
                                        HttpService httpService,
                                        EntitySerializerService entitySerializerService,
                                        DeviceService deviceService) {
        this.applicationContextHolder = applicationContextHolder;
        this.sessionContextHolder = sessionContextHolder;
        this.httpService = httpService;
        this.entitySerializerService = entitySerializerService;
        this.deviceService = deviceService;
    }

    public void savePushToken(String deviceToken) {
        try {
            Map<String, Object> event = RBEvent.create("Collection", applicationContextHolder.getPersistentId(), sessionContextHolder.getSessionIdAndExtendSession())
                    .memberId(sessionContextHolder.getMemberId())
                    .addBody("name", "pushToken")
                    .addBody("type", "fcmToken")
                    .addBody("appType", "fcmAppPush")
                    .addBody("deviceToken", deviceToken)
                    .toMap();
            String serializedEntity = entitySerializerService.serializeToBase64(event);
            httpService.postFormUrlEncoded(serializedEntity);
            RBLogger.log("Received Token: " + deviceToken);
        } catch (Exception e) {
            RBLogger.log("Save Push Token error: " + e.getMessage());
        }
    }

    public void removeTokenAssociation(String deviceToken) {
        try {
            Map<String, Object> event = RBEvent.create("TR", applicationContextHolder.getPersistentId(), sessionContextHolder.getSessionIdAndExtendSession())
                    .memberId(sessionContextHolder.getMemberId())
                    .addBody("name", "pushToken")
                    .addBody("type", "fcmToken")
                    .addBody("appType", "fcmAppPush")
                    .addBody("deviceToken", deviceToken)
                    .toMap();
            String serializedEntity = entitySerializerService.serializeToBase64(event);
            httpService.postFormUrlEncoded(serializedEntity);

            RBLogger.log("Token Removed");
        } catch (Exception e) {
            RBLogger.log("Save Token remove error: " + e.getMessage());
        }
    }

    public void handlePushNotification(Context applicationContext, RemoteMessage remoteMessage) {
        try {
            Map<String, String> data = remoteMessage.getData();
            PushMessageDataWrapper pushMessageDataWrapper = PushMessageDataWrapper.from(data);
            if (pushMessageDataWrapper.getSource().equals(Constants.PUSH_CHANNEL_ID)) {
                sessionContextHolder.updateExternalParameters(pushMessageDataWrapper.toObjectMap());
                this.pushMessageDelivered(pushMessageDataWrapper);

                if (pushMessageDataWrapper.isSilent()) {
                    this.pushMessageOpened(pushMessageDataWrapper);
                    return;
                }

                String notificationChannelId = pushMessageDataWrapper.buildChannelId();
                NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
                    NotificationChannelBuilder.create(deviceService).withChannelId(notificationChannelId).withSound(pushMessageDataWrapper.getSound()).createIn(notificationManager);
                }
                NotificationCompat.Builder notificationCompatBuilder = NotificationCompatBuilder.create(applicationContext, httpService, deviceService)
                        .withChannelId(notificationChannelId)
                        .withApplicationLogo(pushMessageDataWrapper.getApplicationLogo())
                        .withTitle(pushMessageDataWrapper.getTitle())
                        .withSubtitle(pushMessageDataWrapper.getSubTitle())
                        .withMessage(pushMessageDataWrapper.getMessage())
                        .withBadge(pushMessageDataWrapper.getBadge())
                        .withSound(pushMessageDataWrapper.getSound())
                        .withImage(pushMessageDataWrapper.getImageUrl(), pushMessageDataWrapper.getMessage())
                        .withIntent(data)
                        .build();

                notificationManager.notify(0, notificationCompatBuilder.build());
            }
        } catch (Exception e) {
            RBLogger.log("RB Push handle error:" + e.getMessage());
        }
    }

    public void resetBadgeCounts(Context applicationContext) {
        NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }
        }
    }

    public void pushMessageDelivered(PushMessageDataWrapper pushMessageDataWrapper) {
        try {
            Map<String, Object> event = new FeedbackEvent("d",
                    pushMessageDataWrapper.getPushId(),
                    pushMessageDataWrapper.getCampaignId(),
                    pushMessageDataWrapper.getCampaignDate()).toMap();

            String serializedEntity = entitySerializerService.serializeToJson(event);
            httpService.postJsonEncoded(serializedEntity, Constants.PUSH_FEED_BACK_PATH);

        } catch (Exception e) {
            RBLogger.log("Push received event error: " + e.getMessage());
        }
    }

    public void pushMessageOpened(Intent intent) {
        PushMessageDataWrapper pushMessageDataWrapper = PushMessageDataWrapper.from(intent);
        pushMessageOpened(pushMessageDataWrapper);
    }

    protected void pushMessageOpened(PushMessageDataWrapper pushMessageDataWrapper) {
        if (pushMessageDataWrapper.getSource().equals(Constants.PUSH_CHANNEL_ID)) {
            try {
                Map<String, Object> event = new FeedbackEvent("o",
                        pushMessageDataWrapper.getPushId(),
                        pushMessageDataWrapper.getCampaignId(),
                        pushMessageDataWrapper.getCampaignDate()).toMap();

                String serializedEntity = entitySerializerService.serializeToJson(event);
                httpService.postJsonEncoded(serializedEntity, Constants.PUSH_FEED_BACK_PATH);

            } catch (Exception e) {
                RBLogger.log("Push opened event error: " + e.getMessage());
            }
        }
    }
}
