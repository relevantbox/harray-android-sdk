package io.relevantbox.android.event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import io.relevantbox.android.context.ApplicationContextHolder;
import io.relevantbox.android.context.SessionContextHolder;
import io.relevantbox.android.service.DeviceService;
import io.relevantbox.android.service.EntitySerializerService;
import io.relevantbox.android.service.HttpService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SDKEventProcessorHandlerTest {

    @InjectMocks
    private SDKEventProcessorHandler sdkEventProcessorHandler;

    @Mock
    private ApplicationContextHolder applicationContextHolder;
    @Mock
    private SessionContextHolder sessionContextHolder;
    @Mock
    private HttpService httpService;
    @Mock
    private EntitySerializerService entitySerializerService;
    @Mock
    private DeviceService deviceService;

    @Test
    public void it_should_construct_session_start_event_and_make_api_call() throws UnsupportedEncodingException {

        ArgumentCaptor<Map<String, Object>> xennEventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        when(applicationContextHolder.getPersistentId()).thenReturn("persistentId");
        when(applicationContextHolder.getTimezone()).thenReturn("3");

        when(sessionContextHolder.getSessionIdAndExtendSession()).thenReturn("sessionId");
        HashMap<String, Object> externalParameters = new HashMap<>();
        externalParameters.put("utm_source", "xennio");
        when(sessionContextHolder.getExternalParameters()).thenReturn(externalParameters);
        when(sessionContextHolder.getMemberId()).thenReturn(null);
        when(entitySerializerService.serializeToBase64(xennEventArgumentCaptor.capture())).thenReturn("serializedEntity");


        when(deviceService.getManufacturer()).thenReturn("Samsung");
        when(deviceService.getOsVersion()).thenReturn("Kitkat");
        when(deviceService.getBrand()).thenReturn("Galaxy 12");
        when(deviceService.getCarrier()).thenReturn("ATT");
        when(deviceService.getAppVersion()).thenReturn("1.2");
        when(deviceService.getLang()).thenReturn("en");
        when(deviceService.getScreenHeight()).thenReturn(724);
        when(deviceService.getScreenWidth()).thenReturn(1024);

        sdkEventProcessorHandler.sessionStart();

        Map<String, Object> xennEventMap = xennEventArgumentCaptor.getValue();
        Map<String, Object> header = (Map<String, Object>) xennEventMap.get("h");
        Map<String, Object> body = (Map<String, Object>) xennEventMap.get("b");

        assertEquals("SS", header.get("n"));
        assertEquals("sessionId", header.get("s"));
        assertEquals("persistentId", header.get("p"));
        assertEquals("Samsung", body.get("mn"));
        assertEquals("Galaxy 12", body.get("br"));
        assertEquals("ATT", body.get("op"));
        assertEquals("1.2", body.get("av"));
        assertEquals("Android", body.get("os"));
        assertEquals("Kitkat", body.get("osv"));
        assertEquals("3", body.get("zn"));
        assertEquals("en", body.get("ln"));
        assertEquals(724, body.get("sh"));
        assertEquals(1024, body.get("sw"));
        assertEquals("xennio", body.get("utm_source"));
        assertNull(body.get("memberId"));


        verify(httpService).postFormUrlEncoded("serializedEntity");
    }

    @Test
    public void it_should_construct_installation_event_and_make_api_call() throws UnsupportedEncodingException {

        ArgumentCaptor<Map<String, Object>> xennEventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        when(applicationContextHolder.getPersistentId()).thenReturn("persistentId");
        when(sessionContextHolder.getSessionIdAndExtendSession()).thenReturn("sessionId");
        when(sessionContextHolder.getMemberId()).thenReturn("memberId");
        when(entitySerializerService.serializeToBase64(xennEventArgumentCaptor.capture())).thenReturn("serializedEntity");

        sdkEventProcessorHandler.newInstallation();

        Map<String, Object> xennEventMap = xennEventArgumentCaptor.getValue();
        Map<String, Object> header = (Map<String, Object>) xennEventMap.get("h");
        Map<String, Object> body = (Map<String, Object>) xennEventMap.get("b");

        assertEquals("NI", header.get("n"));
        assertEquals("sessionId", header.get("s"));
        assertEquals("persistentId", header.get("p"));
        assertEquals("memberId", body.get("memberId"));

        verify(httpService).postFormUrlEncoded("serializedEntity");
    }

}