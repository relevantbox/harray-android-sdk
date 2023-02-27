package io.relevantbox.android.event;


import java.util.Map;

import io.relevantbox.android.common.Constants;
import io.relevantbox.android.context.ApplicationContextHolder;
import io.relevantbox.android.context.SessionContextHolder;
import io.relevantbox.android.model.RBEvent;
import io.relevantbox.android.service.DeviceService;
import io.relevantbox.android.service.EntitySerializerService;
import io.relevantbox.android.service.HttpService;
import io.relevantbox.android.utils.RBLogger;

public class SDKEventProcessorHandler {
    private final ApplicationContextHolder applicationContextHolder;
    private final SessionContextHolder sessionContextHolder;
    private final HttpService httpService;
    private final EntitySerializerService entitySerializerService;
    private final DeviceService deviceService;



    public SDKEventProcessorHandler(ApplicationContextHolder applicationContextHolder, SessionContextHolder sessionContextHolder, HttpService httpService, EntitySerializerService entitySerializerService, DeviceService deviceService) {
        this.applicationContextHolder = applicationContextHolder;
        this.sessionContextHolder = sessionContextHolder;
        this.httpService = httpService;
        this.entitySerializerService = entitySerializerService;
        this.deviceService = deviceService;
    }

    public void sessionStart() {
        try {
            Map<String, Object> event = RBEvent.create("SS", applicationContextHolder.getPersistentId(), sessionContextHolder.getSessionIdAndExtendSession())
                    .addHeader("sv", applicationContextHolder.getSdkVersion())
                    .memberId(sessionContextHolder.getMemberId())
                    .addBody("os", Constants.ANDROID)
                    .addBody("osv", deviceService.getOsVersion())
                    .addBody("mn", deviceService.getManufacturer())
                    .addBody("br", deviceService.getBrand())
                    .addBody("md", deviceService.getModel())
                    .addBody("op", deviceService.getCarrier())
                    .addBody("av", deviceService.getAppVersion())
                    .addBody("zn", applicationContextHolder.getTimezone())
                    .addBody("sw", deviceService.getScreenWidth())
                    .addBody("sh", deviceService.getScreenHeight())
                    .addBody("ln", deviceService.getLang())
                    .addBody("rt", !applicationContextHolder.isNewInstallation())
                    .appendExtra(sessionContextHolder.getExternalParameters())
                    .toMap();
            String serializedEntity = entitySerializerService.serializeToBase64(event);
            httpService.postFormUrlEncoded(serializedEntity);

        } catch (Exception e) {
            RBLogger.log("Session start error: " + e.getMessage());
        }
    }

    public void newInstallation() {
        try {
            Map<String, Object> event = RBEvent.create("NI", applicationContextHolder.getPersistentId(), sessionContextHolder.getSessionIdAndExtendSession())
                    .memberId(sessionContextHolder.getMemberId())
                    .toMap();
            String serializedEntity = entitySerializerService.serializeToBase64(event);
            httpService.postFormUrlEncoded(serializedEntity);

        } catch (Exception e) {
            RBLogger.log("New Installation error: " + e.getMessage());
        }
    }
}
