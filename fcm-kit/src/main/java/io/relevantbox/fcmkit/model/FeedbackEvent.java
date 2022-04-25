package io.relevantbox.fcmkit.model;

import java.util.HashMap;
import java.util.Map;

public class FeedbackEvent {

    private final String type;
    private final String nonce;
    private final String campaignId;
    private final String customerId;
    private final String pushType;

    public FeedbackEvent(String type, String nonce, String campaignId, String customerId, String pushType) {
        this.type = type;
        this.nonce = nonce;
        this.campaignId = campaignId;
        this.customerId = customerId;
        this.pushType = pushType;

    }

    public Map<String, Object> toMap() {
        Map<String, Object> feedbackEvent = new HashMap<>();
        feedbackEvent.put("n", type);
        feedbackEvent.put("nonce", nonce);
        feedbackEvent.put("campaignId", campaignId);
        feedbackEvent.put("customerId", customerId);
        feedbackEvent.put("pushType", pushType);
        return feedbackEvent;
    }
}
