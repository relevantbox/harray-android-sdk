package io.xenn.android.notification;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.xenn.android.common.PushMessageDataWrapper;
import io.xenn.android.context.ApplicationContextHolder;
import io.xenn.android.context.SessionContextHolder;
import io.xenn.android.model.XennEvent;
import io.xenn.android.service.DeviceService;
import io.xenn.android.service.EntitySerializerService;
import io.xenn.android.service.HttpService;
import io.xenn.android.utils.XennioLogger;

public class NotificationProcessorHandler {

    private final ApplicationContextHolder applicationContextHolder;
    private final SessionContextHolder sessionContextHolder;
    private final HttpService httpService;
    private final EntitySerializerService entitySerializerService;
    private final DeviceService deviceService;


    public NotificationProcessorHandler(ApplicationContextHolder applicationContextHolder, SessionContextHolder sessionContextHolder, HttpService httpService, EntitySerializerService entitySerializerService, DeviceService deviceService) {
        this.applicationContextHolder = applicationContextHolder;
        this.sessionContextHolder = sessionContextHolder;
        this.httpService = httpService;
        this.entitySerializerService = entitySerializerService;
        this.deviceService = deviceService;
    }

    public void savePushToken(String deviceToken) {
        try {
            Map<String, Object> event = XennEvent.create("Collection", applicationContextHolder.getPersistentId(), sessionContextHolder.getSessionIdAndExtendSession())
                    .memberId(sessionContextHolder.getMemberId())
                    .addBody("name", "pushToken")
                    .addBody("type", "fcmToken")
                    .addBody("appType", "fcmAppPush")
                    .addBody("deviceToken", deviceToken)
                    .toMap();
            String serializedEntity = entitySerializerService.serialize(event);
            httpService.postFormUrlEncoded(serializedEntity);
            XennioLogger.log("Received Token: " + deviceToken);
        } catch (Exception e) {
            XennioLogger.log("Save Push Token error: " + e.getMessage());
        }
    }

    public void handlePushNotification(Context applicationContext, RemoteMessage remoteMessage) {
        try {
            Map<String, String> data = remoteMessage.getData();
            PushMessageDataWrapper pushMessageDataWrapper = PushMessageDataWrapper.from(data);
            sessionContextHolder.updateExternalParameters(data);
            sessionContextHolder.updateIntentParameters(data);
            this.pushMessageReceived();

            if (pushMessageDataWrapper.isSilent()) {
                this.pushMessageOpened();
                return;
            }

            String notificationChannelId = pushMessageDataWrapper.buildChannelId();
            NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannelBuilder.create(deviceService).withChannelId(notificationChannelId).withSound(pushMessageDataWrapper.getSound()).createIn(notificationManager);
            NotificationCompat.Builder notificationCompatBuilder = NotificationCompatBuilder.create(applicationContext, httpService, deviceService)
                    .withChannelId(notificationChannelId)
                    .withTitle(pushMessageDataWrapper.getTitle())
                    .withMessage(pushMessageDataWrapper.getMessage())
                    .withBadge(pushMessageDataWrapper.getBadge())
                    .withSound(pushMessageDataWrapper.getSound())
                    .withImage(pushMessageDataWrapper.getImageUrl(), pushMessageDataWrapper.getMessage())
                    .withIntent(data)
                    .build();

            notificationManager.notify(12, notificationCompatBuilder.build());
        } catch (Exception e) {
            XennioLogger.log("Xenn Push handle error:" + e.getMessage());
        }
    }

    protected void pushMessageReceived() {
        try {
            Map<String, Object> event = XennEvent.create("Feedback", applicationContextHolder.getPersistentId(), sessionContextHolder.getSessionIdAndExtendSession())
                    .memberId(sessionContextHolder.getMemberId())
                    .addBody("type", "pushReceived")
                    .appendExtra(sessionContextHolder.getExternalParameters())
                    .toMap();
            String serializedEntity = entitySerializerService.serialize(event);
            httpService.postFormUrlEncoded(serializedEntity);

        } catch (Exception e) {
            XennioLogger.log("Push received event error: " + e.getMessage());
        }
    }

    protected void pushMessageOpened() {
        try {
            Map<String, Object> event = XennEvent.create("Feedback", applicationContextHolder.getPersistentId(), sessionContextHolder.getSessionIdAndExtendSession())
                    .memberId(sessionContextHolder.getMemberId())
                    .addBody("type", "pushOpened")
                    .appendExtra(sessionContextHolder.getExternalParameters())
                    .toMap();
            String serializedEntity = entitySerializerService.serialize(event);
            httpService.postFormUrlEncoded(serializedEntity);

        } catch (Exception e) {
            XennioLogger.log("Push opened event error: " + e.getMessage());
        }
    }
}
