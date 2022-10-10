package io.relevantbox.android.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {
    public static final String LOG_TAG = "RB";
    public static final String SDK_PERSISTENT_ID_KEY = "pid";
    public static final String PREF_COLLECTION_NAME = "RB_PREFS";
    public static final long SESSION_DURATION = 30 * 60 * 1000L;
    public static final List<String> FORBIDDEN_EXTERNAL_PARAMETER_KEYS = Collections.unmodifiableList(Arrays.asList("a", "b", "c", "d", "ts", "n", "s", "p"));
    public static final String UNKNOWN_PROPERTY_VALUE = "UNKNOWN";
    public static final String ANDROID = "Android";
    public static final String RB_API_URL = "https://api.relevantbox.io:443";
    public static final String RB_COLLECTOR_URL = "https://c.relevantbox.io:443";
    public static final String RB_IN_APP_NOTIFICATIONS_URL = "https://inapp.relevantbox.io:443";
}
