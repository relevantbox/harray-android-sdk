package io.relevantbox.android.event.inappnotification;

import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import io.relevantbox.android.utils.RBLogger;

public class JavaScriptInterface {

    static final String JS_OBJ_NAME = "RBAndroid";
    private static final String EVENT_TYPE_KEY = "eventType";
    private static final String CLOSE_POPUP_ACTION = "close";
    private static final String RENDER_COMPLETED_ACTION = "renderCompleted";
    private static final String LINK_CLICK_ACTION = "linkClicked";

    private final InAppNotificationViewManager viewManager;

    public JavaScriptInterface(InAppNotificationViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @JavascriptInterface
    public void postMessage(String message) {
        RBLogger.log("Message from JS: " + message);
        try {
            JSONObject jsonObject = new JSONObject(message);
            String eventType = jsonObject.getString(EVENT_TYPE_KEY);
            if (CLOSE_POPUP_ACTION.equals(eventType)) {
                viewManager.dismiss();
            } else if (RENDER_COMPLETED_ACTION.equals(eventType)) {
                viewManager.adjustHeight();
            } else if (LINK_CLICK_ACTION.equals(eventType)) {
                String link = jsonObject.getString("link");
                viewManager.dismiss();
                viewManager.triggerUserDefinedLinkClickHandler(link);
            } else {
                RBLogger.log("Unhandled JS message: " + message);
            }
        } catch (JSONException e) {
            RBLogger.log("JS message processing error", e);
        }
    }
}