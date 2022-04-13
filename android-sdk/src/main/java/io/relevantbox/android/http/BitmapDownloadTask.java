package io.relevantbox.android.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.URL;

import io.relevantbox.android.utils.IOUtils;
import io.relevantbox.android.utils.RBLogger;

public class BitmapDownloadTask {
    private final String urlString;

    public BitmapDownloadTask(String urlString) {
        this.urlString = urlString;
    }

    public Bitmap getBitmap() {
        InputStream inputStream = null;
        try {
            inputStream = new URL(urlString).openConnection().getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            RBLogger.log("Bitmap download failed " + e.getMessage());
            return null;
        } finally {
            IOUtils.close(inputStream);
        }
    }

    protected String getUrlString() {
        return urlString;
    }
}
