package io.relevantbox.android.common;

import java.util.ArrayList;
import java.util.List;

import io.relevantbox.android.context.RBPlugin;
import io.relevantbox.android.event.inappnotification.LinkClickHandler;
import io.relevantbox.android.model.inappnotification.InAppNotificationHandlerStrategy;
import io.relevantbox.android.utils.UrlUtils;

public final class RBConfig {

    private final String sdkKey;
    private String collectorUrl = Constants.RB_COLLECTOR_URL;
    private String apiUrl = Constants.RB_API_URL;
    private String inAppNotificationsUrl = Constants.RB_API_URL;
    private List<Class<? extends RBPlugin>> rbPlugins = new ArrayList<>();
    private LinkClickHandler inAppNotificationLinkClickHandler;
    private InAppNotificationHandlerStrategy inAppNotificationHandlerStrategy = InAppNotificationHandlerStrategy.PageViewEvent;

    private RBConfig(String sdkKey) {
        this.sdkKey = sdkKey;
    }

    public static RBConfig init(String sdkKey) {
        return new RBConfig(sdkKey);
    }

    public RBConfig apiUrl(String apiUrl) {
        this.apiUrl = UrlUtils.getValidUrl(apiUrl);
        this.inAppNotificationsUrl = UrlUtils.getValidUrl(apiUrl);
        return this;
    }

    public RBConfig collectorUrl(String collectorUrl) {
        this.collectorUrl = UrlUtils.getValidUrl(collectorUrl);
        return this;
    }

    public RBConfig useRBPlugin(Class<? extends RBPlugin> rbPlugin) {
        if (rbPlugin == null) {
            return this;
        }
        if (!this.rbPlugins.contains(rbPlugin)) {
            this.rbPlugins.add(rbPlugin);
        }
        return this;
    }

    public RBConfig inAppNotificationLinkClickHandler(LinkClickHandler linkClickHandler) {
        this.inAppNotificationLinkClickHandler = linkClickHandler;
        return this;
    }

    public RBConfig inAppNotificationHandlerStrategy(InAppNotificationHandlerStrategy inAppNotificationHandlerStrategy) {
        this.inAppNotificationHandlerStrategy = inAppNotificationHandlerStrategy;
        return this;
    }

    public String getSdkKey() {
        return sdkKey;
    }

    public String getCollectorUrl() {
        return collectorUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public List<Class<? extends RBPlugin>> getRbPlugins() {
        return rbPlugins;
    }

    public LinkClickHandler getInAppNotificationLinkClickHandler() {
        return inAppNotificationLinkClickHandler;
    }

    public InAppNotificationHandlerStrategy getInAppNotificationHandlerStrategy() {
        return inAppNotificationHandlerStrategy;
    }

    public String getInAppNotificationsUrl() {
        return inAppNotificationsUrl;
    }
}
