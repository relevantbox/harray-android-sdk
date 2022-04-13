package io.relevantbox.android.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public final class IOUtils {

    public static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                RBLogger.log("Reader closing error: " + e.getMessage());
            }
        }
    }

    public static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                RBLogger.log("Input stream closing error: " + e.getMessage());
            }
        }
    }

    public static String readAll(InputStream inputStream) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            RBLogger.log("Read operation error occurred from InputStream:" + e.getMessage());
            return null;
        } finally {
            close(bufferedReader);
        }
    }
}
