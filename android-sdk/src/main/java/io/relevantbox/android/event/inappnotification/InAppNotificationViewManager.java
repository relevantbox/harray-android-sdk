package io.relevantbox.android.event.inappnotification;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.relevantbox.android.model.inappnotification.InAppNotificationResponse;
import io.relevantbox.android.utils.RBLogger;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class InAppNotificationViewManager {

    private final int horizontalWindowMargin;

    private final Activity activity;
    private final InAppNotificationResponse inAppNotificationResponse;
    private final LinkClickHandler linkClickHandler;
    private final Runnable showHandler;
    private final Runnable closeHandler;
    private final Runnable clickHandler;

    private WebView webView;
    private PopupWindow popupWindow;

    public InAppNotificationViewManager(
            Activity activity,
            InAppNotificationResponse inAppNotificationResponse,
            LinkClickHandler linkClickHandler,
            Runnable showHandler,
            Runnable closeHandler,
            Runnable clickHandler) {
        this.activity = activity;
        this.inAppNotificationResponse = inAppNotificationResponse;
        this.linkClickHandler = linkClickHandler;
        this.showHandler = showHandler;
        this.closeHandler = closeHandler;
        this.clickHandler = clickHandler;
        this.horizontalWindowMargin = dpToPx(0);
    }

    public void show() {
        final String htmlBase64Str = getBase64Str(inAppNotificationResponse);
        if (htmlBase64Str == null) {
            RBLogger.log("No base64 encoded value to be shown as in-app notification.");
            return;
        }
        webView = new WebView(activity);
        webView.clearCache(true);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackground(new ColorDrawable(Color.TRANSPARENT));
        webView.addJavascriptInterface(new JavaScriptInterface(this), JavaScriptInterface.JS_OBJ_NAME);
        webView.loadData(htmlBase64Str, "text/html; charset=utf-8", "base64");

        popupWindow = new PopupWindow(webView, getWindowMaxSizeX(activity), 0);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(
                activity.getWindow().getDecorView().getRootView(),
                getGravity(inAppNotificationResponse),
                0,
                0);
        showHandler.run();
    }

    public void dismiss() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                webView.destroy();
                popupWindow.dismiss();
                closeHandler.run();
            }
        });
    }

    public void adjustHeight() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
                webView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
                if (getGravity(inAppNotificationResponse) == Gravity.FILL) {
                    popupWindow.setHeight(activity.getWindow().getDecorView().getHeight());

                } else {
                    popupWindow.setHeight(dpToPx(webView.getContentHeight()));
                }
                popupWindow.showAtLocation(
                        activity.getWindow().getDecorView().getRootView(),
                        getGravity(inAppNotificationResponse),
                        0,
                        0);
            }
        }, TimeUnit.SECONDS.toMillis(1L));
    }

    public void triggerUserDefinedLinkClickHandler(String link) {
        if (linkClickHandler != null) {
            RBLogger.log("User defined link click handler trigger for link:" + link);
            clickHandler.run();
            linkClickHandler.handle(link);
        } else {
            RBLogger.log("No user defined link click handler defined for link:" + link);
        }
    }

    private String getBase64Str(InAppNotificationResponse inAppNotificationResponse) {
        try {
            return Base64.encodeToString(
                    inAppNotificationResponse.getHtml().getBytes("UTF-8"),
                    Base64.NO_WRAP
            );
        } catch (Exception e) {
            RBLogger.log("In-app notification base64 encoding error occurred.", e);
            return null;
        }
    }

    private int getWindowMaxSizeX(Activity activity) {
        return getDisplaySizePoint(activity).x - horizontalWindowMargin;
    }

    private Point getDisplaySizePoint(@NonNull Activity activity) {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point;
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private int getGravity(InAppNotificationResponse inAppNotificationResponse) {
        if ("center".equals(inAppNotificationResponse.getPosition())) {
            return Gravity.CENTER;
        } else if ("top".equals(inAppNotificationResponse.getPosition())) {
            return Gravity.TOP;
        } else if ("left".equals(inAppNotificationResponse.getPosition())) {
            return Gravity.LEFT;
        } else if ("right".equals(inAppNotificationResponse.getPosition())) {
            return Gravity.RIGHT;
        } else if ("full".equals(inAppNotificationResponse.getPosition())) {
            return Gravity.FILL;
        }
        return Gravity.CENTER;
    }
}