package io.relevantbox.hmskit.notification;

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
import io.relevantbox.android.service.EntitySerializerService;
import io.relevantbox.android.service.HttpService;
import io.relevantbox.hmskit.common.PushMessageDataWrapper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationProcessorHandlerTest {

    @InjectMocks
    private NotificationProcessorHandler notificationProcessorHandler;

    @Mock
    private ApplicationContextHolder applicationContextHolder;

    @Mock
    private SessionContextHolder sessionContextHolder;

    @Mock
    private EntitySerializerService entitySerializerService;

    @Mock
    private HttpService httpService;

    @Test
    public void it_should_construct_save_device_token_event_and_make_api_call() throws UnsupportedEncodingException {

        ArgumentCaptor<Map<String, Object>> rbEventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        when(applicationContextHolder.getPersistentId()).thenReturn("persistentId");
        when(sessionContextHolder.getSessionIdAndExtendSession()).thenReturn("sessionId");
        when(sessionContextHolder.getMemberId()).thenReturn("memberId");
        when(entitySerializerService.serializeToBase64(rbEventArgumentCaptor.capture())).thenReturn("serializedEntity");

        notificationProcessorHandler.savePushToken("device token");

        Map<String, Object> rbEventMap = rbEventArgumentCaptor.getValue();
        Map<String, Object> header = (Map<String, Object>) rbEventMap.get("h");
        Map<String, Object> body = (Map<String, Object>) rbEventMap.get("b");

        assertEquals("Collection", header.get("n"));
        assertEquals("sessionId", header.get("s"));
        assertEquals("persistentId", header.get("p"));
        assertEquals("memberId", body.get("memberId"));
        assertEquals("pushToken", body.get("name"));
        assertEquals("hmsToken", body.get("type"));
        assertEquals("hmsAppPush", body.get("appType"));
        assertEquals("device token", body.get("deviceToken"));

        verify(httpService).postFormUrlEncoded("serializedEntity");
    }

    @Test
    public void it_should_construct_push_message_receive_event_and_make_api_call() {

        ArgumentCaptor<Map<String, Object>> rbEventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        Map<String, String> externalParameters = new HashMap<>();
        externalParameters.put("customerId", "customerId");
        externalParameters.put("campaignId", "campaignId");
        externalParameters.put("nonce", "nonce");
        externalParameters.put("url", "url");
        externalParameters.put("utm_source", "relevantbox");
        externalParameters.put("utm_medium", "utm_medium");
        externalParameters.put("utm_campaign", "utm_campaign");
        externalParameters.put("utm_term", "utm_term");
        externalParameters.put("utm_content", "utm_content");

        when(entitySerializerService.serializeToJson(rbEventArgumentCaptor.capture())).thenReturn("serializedEntity");

        notificationProcessorHandler.pushMessageDelivered(PushMessageDataWrapper.from(externalParameters));

        Map<String, Object> rbEventMap = rbEventArgumentCaptor.getValue();

        assertEquals("d", rbEventMap.get("n"));
        assertEquals("campaignId", rbEventMap.get("campaignId"));
        assertEquals("customerId", rbEventMap.get("customerId"));
        assertEquals("nonce", rbEventMap.get("nonce"));
        assertEquals("hmsAppPush", rbEventMap.get("pushType"));

        verify(httpService).postJsonEncoded("serializedEntity", "feedback");
    }

    @Test
    public void it_should_construct_push_message_open_event_and_make_api_call() throws UnsupportedEncodingException {

        ArgumentCaptor<Map<String, Object>> rbEventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        Map<String, String> externalParameters = new HashMap<>();
        externalParameters.put("customerId", "customerId");
        externalParameters.put("campaignId", "campaignId");
        externalParameters.put("nonce", "nonce");
        externalParameters.put("url", "url");
        externalParameters.put("source", "relevantbox");
        externalParameters.put("utm_medium", "utm_medium");
        externalParameters.put("utm_campaign", "utm_campaign");
        externalParameters.put("utm_term", "utm_term");
        externalParameters.put("utm_content", "utm_content");

        when(entitySerializerService.serializeToJson(rbEventArgumentCaptor.capture())).thenReturn("serializedEntity");

        notificationProcessorHandler.pushMessageOpened(PushMessageDataWrapper.from(externalParameters));

        Map<String, Object> rbEventMap = rbEventArgumentCaptor.getValue();

        assertEquals("o", rbEventMap.get("n"));
        assertEquals("campaignId", rbEventMap.get("campaignId"));
        assertEquals("customerId", rbEventMap.get("customerId"));
        assertEquals("nonce", rbEventMap.get("nonce"));
        assertEquals("hmsAppPush", rbEventMap.get("pushType"));

        verify(httpService).postJsonEncoded("serializedEntity", "feedback");
    }

    @Test
    public void it_should_not_make_push_open_when_source_is_not_xenn_io() {

        Map<String, String> externalParameters = new HashMap<>();
        externalParameters.put("pushId", "pushId");
        externalParameters.put("campaignId", "campaignId");
        externalParameters.put("campaignDate", "campaignDate");
        externalParameters.put("url", "url");
        externalParameters.put("source", "mennio");
        externalParameters.put("utm_medium", "utm_medium");
        externalParameters.put("utm_campaign", "utm_campaign");
        externalParameters.put("utm_term", "utm_term");
        externalParameters.put("utm_content", "utm_content");

        notificationProcessorHandler.pushMessageOpened(PushMessageDataWrapper.from(externalParameters));

        verifyNoInteractions(httpService);
    }

    @Test
    public void it_should_construct_remove_device_token_event_and_make_api_call() throws UnsupportedEncodingException {

        ArgumentCaptor<Map<String, Object>> rbEventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        when(applicationContextHolder.getPersistentId()).thenReturn("persistentId");
        when(sessionContextHolder.getSessionIdAndExtendSession()).thenReturn("sessionId");
        when(sessionContextHolder.getMemberId()).thenReturn("memberId");
        when(entitySerializerService.serializeToBase64(rbEventArgumentCaptor.capture())).thenReturn("serializedEntity");

        notificationProcessorHandler.removeTokenAssociation("device token");

        Map<String, Object> rbEventMap = rbEventArgumentCaptor.getValue();
        Map<String, Object> header = (Map<String, Object>) rbEventMap.get("h");
        Map<String, Object> body = (Map<String, Object>) rbEventMap.get("b");

        assertEquals("TR", header.get("n"));
        assertEquals("sessionId", header.get("s"));
        assertEquals("persistentId", header.get("p"));
        assertEquals("memberId", body.get("memberId"));
        assertEquals("pushToken", body.get("name"));
        assertEquals("hmsToken", body.get("type"));
        assertEquals("hmsAppPush", body.get("appType"));
        assertEquals("device token", body.get("deviceToken"));

        verify(httpService).postFormUrlEncoded("serializedEntity");
    }

}