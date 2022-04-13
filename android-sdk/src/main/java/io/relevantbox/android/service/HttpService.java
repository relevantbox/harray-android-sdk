package io.relevantbox.android.service;

import android.graphics.Bitmap;

import java.util.Map;

import io.relevantbox.android.common.ResponseBodyHandler;
import io.relevantbox.android.common.ResultConsumer;
import io.relevantbox.android.http.BitmapDownloadTask;
import io.relevantbox.android.http.HttpRequestFactory;
import io.relevantbox.android.http.PostFormUrlEncodedTask;
import io.relevantbox.android.http.PostJsonEncodedTask;
import io.relevantbox.android.utils.RBLogger;

import static io.relevantbox.android.utils.UrlUtils.appendPath;

public class HttpService {

    private final String sdkKey;
    private final String collectorUrl;
    private final String apiUrl;
    private final HttpRequestFactory httpRequestFactory;

    public HttpService(HttpRequestFactory httpRequestFactory, String sdkKey, String collectorUrl, String apiUrl) {
        this.httpRequestFactory = httpRequestFactory;
        this.sdkKey = sdkKey;
        this.collectorUrl = collectorUrl;
        this.apiUrl = apiUrl;
    }

    public <T> void getApiRequest(String path,
                                  Map<String, Object> params,
                                  ResponseBodyHandler<T> rh,
                                  ResultConsumer<T> callback) {
        httpRequestFactory
                .getHttpGetTask(getApiUrl(path, params), rh, callback)
                .execute();
    }

    public void postFormUrlEncoded(final String payload) {
        PostFormUrlEncodedTask task = httpRequestFactory.getPostFormUrlEncodedTask(getCollectorUrl(), "e=" + payload);
        task.execute();
    }

    public Bitmap downloadImage(String endpoint) {
        BitmapDownloadTask bitmapDownloadTask = httpRequestFactory.getBitmapDownloadTask(endpoint);
        return bitmapDownloadTask.getBitmap();
    }

    public void postJsonEncoded(final String payload, final String path) {
        PostJsonEncodedTask postJsonEncodedTask = httpRequestFactory.getPostJsonEncodedTask(getCollectorUrl(path), payload);
        postJsonEncodedTask.execute();
    }

    public String getCollectorUrl() {
        return appendPath(collectorUrl, sdkKey);
    }

    public String getCollectorUrl(String feedback) {
        return appendPath(collectorUrl, feedback);
    }

    private String getApiUrl(String path, Map<String, Object> params) {
        StringBuilder paramsAsStr = new StringBuilder("?");
        for (Map.Entry<String, Object> paramEntry : params.entrySet()) {
            paramsAsStr
                    .append(paramEntry.getKey())
                    .append("=")
                    .append(paramEntry.getValue())
                    .append("&");
        }
        String url = appendPath(this.apiUrl, path) + paramsAsStr.toString();
        RBLogger.log("Url will be called" + url);
        return url;
    }
}
