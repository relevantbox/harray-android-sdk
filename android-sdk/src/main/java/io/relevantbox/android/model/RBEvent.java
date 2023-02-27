package io.relevantbox.android.model;

import java.util.HashMap;
import java.util.Map;

public class RBEvent {

    private final Map<String, Object> h = new HashMap<>();
    private final Map<String, Object> b = new HashMap<>();

    private RBEvent() {
    }

    public static RBEvent create(String name, String persistentId, String sessionId) {
        RBEvent rbEvent = new RBEvent();
        rbEvent.h.put("n", name);
        rbEvent.h.put("p", persistentId);
        rbEvent.h.put("s", sessionId);
        return rbEvent;
    }

    public RBEvent addHeader(String key, Object value) {
        h.put(key, value);
        return this;
    }

    public RBEvent addBody(String key, Object value) {
        b.put(key, value);
        return this;
    }

    public RBEvent memberId(String memberId) {
        if (memberId != null && !"".equalsIgnoreCase(memberId)) {
            this.addBody("memberId", memberId);
        }
        return this;
    }

    public RBEvent appendExtra(Map<String, Object> params) {
        if (params != null) {
            this.b.putAll(params);
        }
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("h", h);
        map.put("b", b);
        return map;
    }

    public String getStringParameterValue(String key) {
        if (h.containsKey(key)) {
            return h.get(key).toString();
        }
        return null;
    }

    public Double getDoubleParameterValue(String key) {
        if (h.containsKey(key)) {
            return Double.valueOf(h.get(key).toString());
        }
        return null;
    }
}
