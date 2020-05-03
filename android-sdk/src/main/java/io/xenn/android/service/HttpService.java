package io.xenn.android.service;

import android.graphics.Bitmap;

import io.xenn.android.http.BitmapDownloadTask;
import io.xenn.android.http.HttpRequestFactory;
import io.xenn.android.http.PostFormUrlEncodedTask;
import io.xenn.android.http.PostJsonEncodedTask;

public class HttpService {

    private final String endpoint;
    private final HttpRequestFactory httpRequestFactory;

    public HttpService(String endpoint, HttpRequestFactory httpRequestFactory) {
        this.endpoint = endpoint;
        this.httpRequestFactory = httpRequestFactory;
    }

    public void postFormUrlEncoded(final String payload) {
        PostFormUrlEncodedTask task = httpRequestFactory.getPostFormUrlEncodedTask(endpoint, "e=" + payload);
        task.execute();
    }

    public Bitmap downloadImage(String endpoint) {
        BitmapDownloadTask bitmapDownloadTask = httpRequestFactory.getBitmapDownloadTask(endpoint);
        return bitmapDownloadTask.getBitmap();
    }

    public void postJsonEncoded(final String payload, final String path) {
        PostJsonEncodedTask postJsonEncodedTask = httpRequestFactory.getPostJsonEncodedTask(endpoint + "/" + path, payload);
        postJsonEncodedTask.execute();
    }
}
