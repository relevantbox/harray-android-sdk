package io.relevantbox.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Map;

import io.relevantbox.android.common.Constants;
import io.relevantbox.android.common.RBConfig;
import io.relevantbox.android.context.ActivityLifecycleListener;
import io.relevantbox.android.context.ApplicationContextHolder;
import io.relevantbox.android.context.SessionContextHolder;
import io.relevantbox.android.context.SessionState;
import io.relevantbox.android.context.RBPluginRegistry;
import io.relevantbox.android.event.BrowsingHistoryProcessorHandler;
import io.relevantbox.android.event.ChainProcessorHandler;
import io.relevantbox.android.event.EcommerceEventProcessorHandler;
import io.relevantbox.android.event.EventProcessorHandler;
import io.relevantbox.android.event.PushMessagesHistoryProcessorHandler;
import io.relevantbox.android.event.inappnotification.InAppNotificationProcessorHandler;
import io.relevantbox.android.event.RecommendationProcessorHandler;
import io.relevantbox.android.event.SDKEventProcessorHandler;
import io.relevantbox.android.http.HttpRequestFactory;
import io.relevantbox.android.model.inappnotification.InAppNotificationHandlerStrategy;
import io.relevantbox.android.service.DeviceService;
import io.relevantbox.android.service.EncodingService;
import io.relevantbox.android.service.EntitySerializerService;
import io.relevantbox.android.service.HttpService;
import io.relevantbox.android.service.JsonDeserializerService;
import io.relevantbox.android.service.JsonSerializerService;
import io.relevantbox.android.utils.RBLogger;

public final class RB {

    private EntitySerializerService entitySerializerService;
    protected EventProcessorHandler eventProcessorHandler;
    protected SDKEventProcessorHandler sdkEventProcessorHandler;
    protected SessionContextHolder sessionContextHolder;
    protected ApplicationContextHolder applicationContextHolder;
    protected EcommerceEventProcessorHandler ecommerceEventProcessorHandler;
    protected RecommendationProcessorHandler recommendationProcessorHandler;
    protected BrowsingHistoryProcessorHandler browsingHistoryProcessorHandler;
    protected InAppNotificationProcessorHandler inAppNotificationProcessorHandler;
    protected PushMessagesHistoryProcessorHandler pushMessagesHistoryProcessorHandler;
    protected HttpService httpService;
    protected HttpService inAppNotificationsHttpService;
    protected DeviceService deviceService;
    protected RBPluginRegistry rbPluginRegistry;

    private static RB instance;

    private RB(Context context, RBConfig rbConfig) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE);
        this.applicationContextHolder = new ApplicationContextHolder(sharedPreferences);
        this.sessionContextHolder = new SessionContextHolder();

        this.httpService = new HttpService(new HttpRequestFactory(), rbConfig.getSdkKey(), rbConfig.getCollectorUrl(), rbConfig.getApiUrl());
        this.inAppNotificationsHttpService = new HttpService(new HttpRequestFactory(), rbConfig.getSdkKey(), rbConfig.getCollectorUrl(), rbConfig.getInAppNotificationsUrl());
        this.entitySerializerService = new EntitySerializerService(new EncodingService(), new JsonSerializerService());
        ChainProcessorHandler chainProcessorHandler = new ChainProcessorHandler();
        EventProcessorHandler eventProcessorHandler = new EventProcessorHandler(applicationContextHolder, sessionContextHolder, httpService, entitySerializerService, chainProcessorHandler);
        this.eventProcessorHandler = eventProcessorHandler;

        this.deviceService = new DeviceService(context);
        this.sdkEventProcessorHandler = new SDKEventProcessorHandler(applicationContextHolder, sessionContextHolder, httpService, entitySerializerService, deviceService);

        this.ecommerceEventProcessorHandler = new EcommerceEventProcessorHandler(eventProcessorHandler);

        JsonDeserializerService jsonDeserializerService = new JsonDeserializerService();
        this.recommendationProcessorHandler = new RecommendationProcessorHandler(applicationContextHolder, sessionContextHolder, httpService, rbConfig.getSdkKey(), jsonDeserializerService);
        this.browsingHistoryProcessorHandler = new BrowsingHistoryProcessorHandler(applicationContextHolder, sessionContextHolder, httpService, rbConfig.getSdkKey(), jsonDeserializerService);
        this.inAppNotificationProcessorHandler = new InAppNotificationProcessorHandler(
                eventProcessorHandler, applicationContextHolder, sessionContextHolder, inAppNotificationsHttpService, jsonDeserializerService, rbConfig);
        this.pushMessagesHistoryProcessorHandler = new PushMessagesHistoryProcessorHandler(sessionContextHolder, httpService, rbConfig.getSdkKey(), jsonDeserializerService);

        this.rbPluginRegistry = new RBPluginRegistry();

        if (rbConfig.getInAppNotificationHandlerStrategy() == InAppNotificationHandlerStrategy.PageViewEvent) {
            chainProcessorHandler.addHandler(inAppNotificationProcessorHandler);
        }
    }

    public static void configure(Context context, @NonNull RBConfig rbConfig) {
        instance = new RB(context, rbConfig);
        plugins().initAll(rbConfig.getRbPlugins());
        plugins().onCreate(context);
        registerActivityLifecycleListener(context);
    }

    public static EventProcessorHandler eventing() {
        SessionContextHolder sessionContextHolder = getInstance().sessionContextHolder;
        if (sessionContextHolder.getSessionState() != SessionState.SESSION_STARTED) {
            getInstance().sdkEventProcessorHandler.sessionStart();
            sessionContextHolder.startSession();
            if (getInstance().applicationContextHolder.isNewInstallation()) {
                getInstance().sdkEventProcessorHandler.newInstallation();
                getInstance().applicationContextHolder.setInstallationCompleted();
            }

        }
        return getInstance().eventProcessorHandler;
    }

    public static EcommerceEventProcessorHandler ecommerce() {
        return getInstance().ecommerceEventProcessorHandler;
    }

    public static RecommendationProcessorHandler recommendations() {
        return getInstance().recommendationProcessorHandler;
    }

    public static BrowsingHistoryProcessorHandler browsingHistory() {
        return getInstance().browsingHistoryProcessorHandler;
    }

    public static InAppNotificationProcessorHandler inAppNotifications() {
        return getInstance().inAppNotificationProcessorHandler;
    }

    public static PushMessagesHistoryProcessorHandler pushMessagesHistory() {
        return getInstance().pushMessagesHistoryProcessorHandler;
    }

    public static RBPluginRegistry plugins() {
        return getInstance().rbPluginRegistry;
    }

    public static void synchronizeIntentData(Map<String, Object> intentData) {
        getInstance().sessionContextHolder.updateExternalParameters(intentData);
    }

    protected static RB getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Xennio.configure(Context context, String sdkKey, String collectorUrl) must be called before getting instance");
        }
        return instance;
    }

    public static void login(String memberId) {
        RB instance = getInstance();
        if (memberId != null && !"".equals(memberId) && !memberId.equals(instance.sessionContextHolder.getMemberId())) {
            instance.sessionContextHolder.login(memberId);
            instance.sessionContextHolder.restartSession();
            instance.rbPluginRegistry.onLogin();
        }
    }

    public static void logout() {
        RB instance = getInstance();
        instance.rbPluginRegistry.onLogout();
        instance.sessionContextHolder.logout();
        instance.sessionContextHolder.restartSession();
    }

    public static EntitySerializerService getEntitySerializerService() {
        return getInstance().entitySerializerService;
    }

    public static ApplicationContextHolder getApplicationContextHolder() {
        return getInstance().applicationContextHolder;
    }

    public static SessionContextHolder getSessionContextHolder() {
        return getInstance().sessionContextHolder;
    }

    public static HttpService getHttpService() {
        return getInstance().httpService;
    }

    public static DeviceService getDeviceService() {
        return getInstance().deviceService;
    }

    private static void registerActivityLifecycleListener(Context context) {
        if (context instanceof Application) {
            ((Application) context).registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
        } else {
            RBLogger.log("context parameter is not Application type");
        }
    }
}