package io.relevantbox.android.event;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.relevantbox.android.common.ResponseBodyHandler;
import io.relevantbox.android.common.ResultConsumer;
import io.relevantbox.android.context.ApplicationContextHolder;
import io.relevantbox.android.context.SessionContextHolder;
import io.relevantbox.android.service.HttpService;
import io.relevantbox.android.service.JsonDeserializerService;

public class BrowsingHistoryProcessorHandler {

    private final ApplicationContextHolder applicationContextHolder;
    private final SessionContextHolder sessionContextHolder;
    private final HttpService httpService;
    private final String sdkKey;
    private final JsonDeserializerService jsonDeserializerService;

    public BrowsingHistoryProcessorHandler(
            ApplicationContextHolder applicationContextHolder,
            SessionContextHolder sessionContextHolder,
            HttpService httpService,
            String sdkKey,
            JsonDeserializerService jsonDeserializerService) {
        this.applicationContextHolder = applicationContextHolder;
        this.sessionContextHolder = sessionContextHolder;
        this.httpService = httpService;
        this.sdkKey = sdkKey;
        this.jsonDeserializerService = jsonDeserializerService;
    }

    public void getBrowsingHistory(@NonNull String entityName,
                                   int size,
                                   @NonNull ResultConsumer<List<Map<String, String>>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("sdkKey", sdkKey);
        params.put("pid", applicationContextHolder.getPersistentId());
        params.put("entityName", entityName);
        if (sessionContextHolder.getMemberId() != null) {
            params.put("memberId", sessionContextHolder.getMemberId());
        }
        params.put("size", String.valueOf(size));
        httpService.getApiRequest("/browsing-history", params, new ResponseBodyHandler<List<Map<String, String>>>() {
            @Override
            public List<Map<String, String>> handle(String rawResponseBody) {
                return jsonDeserializerService.deserializeToMapList(rawResponseBody);
            }
        }, callback);
    }
}
