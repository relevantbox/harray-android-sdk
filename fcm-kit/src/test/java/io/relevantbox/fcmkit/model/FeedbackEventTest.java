package io.relevantbox.fcmkit.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Map;

public class FeedbackEventTest {

    @Test
    public void it_should_create_map_from_fields() {
        FeedbackEvent feedbackEvent = new FeedbackEvent("o", "1", "campaignId", "customerId");
        Map<String, Object> result = feedbackEvent.toMap();

        assertEquals("o", result.get("n"));
        assertEquals("1", result.get("nonce"));
        assertEquals("campaignId", result.get("campaignId"));
        assertEquals("customerId", result.get("customerId"));
        assertEquals("fcmAppPush", result.get("pushType"));
    }

}