package io.relevantbox.android.event.inappnotification;

import android.app.Activity;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.relevantbox.android.common.ResponseBodyHandler;
import io.relevantbox.android.common.ResultConsumer;
import io.relevantbox.android.common.RBConfig;
import io.relevantbox.android.context.ActivityLifecycleListener;
import io.relevantbox.android.context.ApplicationContextHolder;
import io.relevantbox.android.context.SessionContextHolder;
import io.relevantbox.android.event.AfterPageViewEventHandler;
import io.relevantbox.android.event.EventProcessorHandler;
import io.relevantbox.android.model.RBEvent;
import io.relevantbox.android.model.inappnotification.InAppNotificationHandlerStrategy;
import io.relevantbox.android.model.inappnotification.InAppNotificationResponse;
import io.relevantbox.android.service.DeviceService;
import io.relevantbox.android.service.HttpService;
import io.relevantbox.android.service.JsonDeserializerService;
import io.relevantbox.android.utils.RBLogger;

public class InAppNotificationProcessorHandler implements AfterPageViewEventHandler {

    private static final Long CHECK_NOTIFICATION_INTERVAL = 20 * 1000L;

    private Timer timer = new Timer();

    private final EventProcessorHandler eventProcessorHandler;
    private final HttpService httpService;
    private final ApplicationContextHolder applicationContextHolder;
    private final JsonDeserializerService jsonDeserializerService;
    private final SessionContextHolder sessionContextHolder;
    private final RBConfig rbConfig;
    private final DeviceService deviceService;
    private final Map<String, Object> requestParameters = new HashMap<>();

    public InAppNotificationProcessorHandler(
            EventProcessorHandler eventProcessorHandler,
            ApplicationContextHolder applicationContextHolder,
            SessionContextHolder sessionContextHolder,
            HttpService httpService,
            JsonDeserializerService jsonDeserializerService,
            RBConfig rbConfig,
            DeviceService deviceService) {
        this.eventProcessorHandler = eventProcessorHandler;
        this.httpService = httpService;
        this.rbConfig = rbConfig;
        this.jsonDeserializerService = jsonDeserializerService;
        this.applicationContextHolder = applicationContextHolder;
        this.sessionContextHolder = sessionContextHolder;
        this.deviceService = deviceService;

        if (this.rbConfig.getInAppNotificationHandlerStrategy() == InAppNotificationHandlerStrategy.TimerBased) {
            ProcessLifecycleOwner.get().getLifecycle().addObserver(
                    new LifecycleObserver() {
                        @OnLifecycleEvent(Lifecycle.Event.ON_START)
                        void onMoveToForeground() {
                            scheduleTimer();
                        }

                        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                        void onMoveToBackground() {
                            cancelTimer();
                        }
                    });
        }

        requestParameters.put("source", "android");

    }

    private void scheduleTimer() {
        timer.purge();
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                callAfter(null);
            }
        }, 1L, CHECK_NOTIFICATION_INTERVAL);
        RBLogger.log("Xenn in-app notification task initialized");
    }

    private void cancelTimer() {
        timer.purge();
        timer.cancel();
        RBLogger.log("Xenn in-app notification task cancelled");
    }

    private void showInAppNotification(@Nullable InAppNotificationResponse inAppNotificationResponse) {
        if (inAppNotificationResponse == null) {
            RBLogger.log("There is no in-app notification response to be processed");
            return;
        }
        Activity activity = ActivityLifecycleListener.getCurrentActivity();
        if (activity == null) {
            RBLogger.log("There is activity to show in-app notification");
            return;
        }
        delayShowUntilAvailable(activity, inAppNotificationResponse);
    }

    private void delayShowUntilAvailable(final Activity activity, final InAppNotificationResponse inAppNotificationResponse) {
        if (isActivityReady(activity)) {
            new InAppNotificationViewManager(
                    activity, inAppNotificationResponse, rbConfig.getInAppNotificationLinkClickHandler(), createShowEventHandler(inAppNotificationResponse), createCloseEventHandler(inAppNotificationResponse)
            ).show();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    delayShowUntilAvailable(activity, inAppNotificationResponse);
                }
            }, 50);
        }
    }

    private boolean isActivityReady(@NonNull Activity activity) {
        return activity.getWindow().getDecorView().getApplicationWindowToken() != null;
    }

    private Runnable createShowEventHandler(final InAppNotificationResponse inAppNotificationResponse) {
        return new Runnable() {
            @Override
            public void run() {
                Map<String, Object> eventParams = new HashMap<>();
                eventParams.put("entity", "banners");
                eventParams.put("id", inAppNotificationResponse.getId());
                eventProcessorHandler.impression("bannerShow", eventParams);
            }
        };
    }

    private Runnable createCloseEventHandler(final InAppNotificationResponse inAppNotificationResponse) {
        return new Runnable() {
            @Override
            public void run() {
                Map<String, Object> eventParams = new HashMap<>();
                eventParams.put("entity", "banners");
                eventParams.put("id", inAppNotificationResponse.getId());
                eventParams.put("action", "close");
                eventProcessorHandler.actionResult("bannerClose", eventParams);
            }
        };
    }

    @Override
    public void callAfter(RBEvent event) {
        RBLogger.log("Trying to get xenn in-app notification");
        requestParameters.put("sdkKey", rbConfig.getSdkKey());
        requestParameters.put("pid", applicationContextHolder.getPersistentId());
        requestParameters.put("deviceLang", deviceService.getLang());
        if (sessionContextHolder.getMemberId() != null) {
            requestParameters.put("memberId", sessionContextHolder.getMemberId());
        }
        String pageType = event.getStringParameterValue("pageType");
        if (pageType != null) {
            requestParameters.put("pageType", pageType);
        }

        String entity = event.getStringParameterValue("entity");
        if (entity != null) {
            requestParameters.put("entity", entity);
        }

        String entityId = event.getStringParameterValue("entityId");
        if (entityId != null) {
            requestParameters.put("entityId", entityId);
        }

        String collectionId = event.getStringParameterValue("collectionId");
        if (collectionId != null) {
            requestParameters.put("collectionId", collectionId);
        }

        Double price = event.getDoubleParameterValue("price");
        if (price != null) {
            requestParameters.put("price", price);
        }

        ResultConsumer<InAppNotificationResponse> callback = new ResultConsumer<InAppNotificationResponse>() {
            @Override
            public void consume(InAppNotificationResponse data) {
                showInAppNotification(data);
            }
        };
        httpService.getApiRequest("/in-app-notifications", requestParameters, new ResponseBodyHandler<InAppNotificationResponse>() {
            @Override
            public InAppNotificationResponse handle(String rawResponseBody) {
                return InAppNotificationResponse.fromMap(jsonDeserializerService.deserializeToMap(rawResponseBody));
            }
        }, callback);
    }
}
