package io.relevantbox.hmskit.model;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FeedbackEventTest {

    @Test
    public void it_should_create_map_from_fields() {
        FeedbackEvent feedbackEvent = new FeedbackEvent("o", "1", "campaignId", "customerId", "pushType");
        Map<String, Object> result = feedbackEvent.toMap();

        assertEquals("o", result.get("n"));
        assertEquals("1", result.get("nonce"));
        assertEquals("campaignId", result.get("campaignId"));
        assertEquals("customerId", result.get("customerId"));
        assertEquals("pushType", result.get("pushType"));

    }

}