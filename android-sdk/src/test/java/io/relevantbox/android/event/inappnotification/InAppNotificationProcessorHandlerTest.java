package io.relevantbox.android.event.inappnotification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import io.relevantbox.android.common.ResponseBodyHandler;
import io.relevantbox.android.common.ResultConsumer;
import io.relevantbox.android.common.RBConfig;
import io.relevantbox.android.context.ApplicationContextHolder;
import io.relevantbox.android.context.SessionContextHolder;
import io.relevantbox.android.event.EventProcessorHandler;
import io.relevantbox.android.model.RBEvent;
import io.relevantbox.android.service.DeviceService;
import io.relevantbox.android.service.HttpService;
import io.relevantbox.android.service.JsonDeserializerService;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InAppNotificationProcessorHandlerTest {

    @Mock
    private EventProcessorHandler eventProcessorHandler;

    @Mock
    private ApplicationContextHolder applicationContextHolder;

    @Mock
    private SessionContextHolder sessionContextHolder;

    @Mock
    private HttpService httpService;

    @Mock
    private JsonDeserializerService jsonDeserializerService;

    @Captor
    private ArgumentCaptor<Map<String, Object>> paramCaptor;

    @Mock
    private LinkClickHandler linkClickHandler;

    @Mock
    private DeviceService deviceService;


    @Test
    public void it_should_get_in_app_notifications() {
        RBConfig rbConfig = RBConfig.init("sdk-key").inAppNotificationLinkClickHandler(linkClickHandler);

        InAppNotificationProcessorHandler inAppNotificationProcessorHandler = new InAppNotificationProcessorHandler(
                eventProcessorHandler, applicationContextHolder, sessionContextHolder, httpService, jsonDeserializerService, rbConfig,
                deviceService);

        when(applicationContextHolder.getPersistentId()).thenReturn("pid");
        when(sessionContextHolder.getMemberId()).thenReturn("memberId");
        inAppNotificationProcessorHandler.callAfter(RBEvent.create("pageView", "", ""));

        verify(httpService).getApiRequest(eq("/in-app-notifications"), paramCaptor.capture(), any(ResponseBodyHandler.class), any(ResultConsumer.class));
        Map<String, Object> capturedParams = paramCaptor.getValue();
        assertEquals(capturedParams.get("sdkKey"), "sdk-key");
        assertEquals(capturedParams.get("source"), "android");
        assertEquals(capturedParams.get("pid"), "pid");
        assertEquals(capturedParams.get("memberId"), "memberId");
    }

    @Test
    public void it_should_get_in_app_notifications_without_memberId_if_not_exists() {

        RBConfig rbConfig = RBConfig.init("sdk-key").inAppNotificationLinkClickHandler(linkClickHandler);

        InAppNotificationProcessorHandler inAppNotificationProcessorHandler = new InAppNotificationProcessorHandler(
                eventProcessorHandler, applicationContextHolder, sessionContextHolder, httpService, jsonDeserializerService, rbConfig,
                deviceService);

        when(applicationContextHolder.getPersistentId()).thenReturn("pid");
        when(sessionContextHolder.getMemberId()).thenReturn(null);
        when(deviceService.getLang()).thenReturn("en");
        inAppNotificationProcessorHandler.callAfter(RBEvent.create("pageView", "", ""));


        verify(httpService).getApiRequest(eq("/in-app-notifications"), paramCaptor.capture(), any(ResponseBodyHandler.class), any(ResultConsumer.class));
        Map<String, Object> capturedParams = paramCaptor.getValue();
        assertEquals(capturedParams.get("sdkKey"), "sdk-key");
        assertEquals(capturedParams.get("source"), "android");
        assertEquals(capturedParams.get("deviceLanguage"), "en");
        assertEquals(capturedParams.get("pid"), "pid");
        assertEquals(capturedParams.get("memberId"), null);
    }

    @Test
    public void it_should_add_page_type_when_page_type_exists() {

        RBConfig rbConfig = RBConfig.init("sdk-key").inAppNotificationLinkClickHandler(linkClickHandler);

        InAppNotificationProcessorHandler inAppNotificationProcessorHandler = new InAppNotificationProcessorHandler(
                eventProcessorHandler, applicationContextHolder, sessionContextHolder, httpService, jsonDeserializerService, rbConfig,
                deviceService);

        when(applicationContextHolder.getPersistentId()).thenReturn("pid");
        when(sessionContextHolder.getMemberId()).thenReturn(null);
        when(deviceService.getLang()).thenReturn("en");
        inAppNotificationProcessorHandler.callAfter(RBEvent.create("pageView", "", "").addBody("pageType", "homePage"));

        verify(httpService).getApiRequest(eq("/in-app-notifications"), paramCaptor.capture(), any(ResponseBodyHandler.class), any(ResultConsumer.class));
        Map<String, Object> capturedParams = paramCaptor.getValue();
        assertEquals(capturedParams.get("sdkKey"), "sdk-key");
        assertEquals(capturedParams.get("source"), "android");
        assertEquals(capturedParams.get("deviceLanguage"), "en");
        assertEquals(capturedParams.get("pid"), "pid");
        assertEquals(capturedParams.get("pageType"), "homePage");
        assertEquals(capturedParams.get("memberId"), null);
    }
}