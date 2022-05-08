package io.relevantbox.hmskit.model;

import java.util.HashMap;
import java.util.Map;

public class FeedbackEvent {

    private final String type;
    private final String nonce;
    private final String campaignId;
    private final String customerId;

    public FeedbackEvent(String type, String nonce, String campaignId, String customerId) {
        this.type = type;
        this.nonce = nonce;
        this.campaignId = campaignId;
        this.customerId = customerId;

    }

    public Map<String, Object> toMap() {
        Map<String, Object> feedbackEvent = new HashMap<>();
        feedbackEvent.put("n", type);
        feedbackEvent.put("nonce", nonce);
        feedbackEvent.put("campaignId", campaignId);
        feedbackEvent.put("customerId", customerId);
        feedbackEvent.put("pushType", "hmsAppPush");
        return feedbackEvent;
    }
}