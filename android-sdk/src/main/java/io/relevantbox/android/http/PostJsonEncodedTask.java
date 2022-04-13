package io.relevantbox.android.http;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.relevantbox.android.utils.RBLogger;

public class PostJsonEncodedTask extends AsyncTask<Void, Void, Integer> {

    private final String payload;
    private final String endpoint;

    public PostJsonEncodedTask(String endpoint, String payload) {
        this.payload = payload;
        this.endpoint = endpoint;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            URL url = new URL(endpoint);
            byte[] postData = payload.getBytes();
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
            urlConnection.setUseCaches(false);

            DataOutputStream dStream = new DataOutputStream(urlConnection.getOutputStream());
            dStream.write(postData);
            dStream.flush();
            dStream.close();
            int responseCode = urlConnection.getResponseCode();
            RBLogger.log("Xenn API request completed with status code:" + responseCode);
            return responseCode;
        } catch (Exception e) {
            RBLogger.log("Xenn API request failed" + e.getMessage());
        }
        return 0;
    }

    public String getPayload() {
        return payload;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
