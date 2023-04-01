package io.relevantbox.android.model.inappnotification;

import androidx.annotation.Nullable;

import java.util.Map;

public class InAppNotificationResponse {

    private String id;
    private String style;
    private String html;
    private String imageUrl;
    private String position;

    @Nullable
    public static InAppNotificationResponse fromMap(Map<String, String> map) {
        if (map.isEmpty()) {
            return null;
        }
        InAppNotificationResponse response = new InAppNotificationResponse();
        response.id = map.get("id");
        response.style = map.get("style");
        response.html = map.get("html");
        response.imageUrl = map.get("imageUrl");
        if(map.containsKey("position")){
            response.position = map.get("position");
        }
        return response;
    }

    public String getId() {
        return id;
    }

    public String getStyle() {
        return style;
    }

    public String getHtml() {
        return html;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}