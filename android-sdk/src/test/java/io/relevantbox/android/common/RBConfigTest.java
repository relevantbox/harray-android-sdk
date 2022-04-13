package io.relevantbox.android.common;

import org.junit.Test;

import java.util.Collections;

import io.relevantbox.android.model.TestRBPlugin;

import static org.junit.Assert.assertEquals;

public class RBConfigTest {

    @Test
    public void it_should_build_xennConfig_with_mandatory_fields() {
        RBConfig rbConfig = RBConfig.init("sdkKey");

        assertEquals("https://api.xenn.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://c.xenn.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.emptyList(), rbConfig.getRbPlugins());
    }

    @Test
    public void it_should_build_xennConfig_with_custom_apiUrl() {
        RBConfig rbConfig = RBConfig.init("sdkKey")
                .apiUrl("https://custom.xenn.io:443")
                .collectorUrl("https://customcollector.xenn.io:443");

        assertEquals("https://custom.xenn.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://customcollector.xenn.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.emptyList(), rbConfig.getRbPlugins());
    }

    @Test
    public void it_should_build_xennConfig_with_xennPlugin() {
        RBConfig rbConfig = RBConfig.init("sdkKey")
                .useRBPlugin(TestRBPlugin.class);

        assertEquals("https://api.xenn.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://c.xenn.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.singletonList(TestRBPlugin.class), rbConfig.getRbPlugins());
    }

    @Test
    public void it_should_build_xennConfig_without_xennPlugin_when_plugin_is_null() {
        RBConfig rbConfig = RBConfig.init("sdkKey")
                .useRBPlugin(null);

        assertEquals("https://api.xenn.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://c.xenn.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.emptyList(), rbConfig.getRbPlugins());
    }

    @Test
    public void it_should_build_xennConfig_with_only_single_xennPlugin_when_recurring_add() {
        RBConfig rbConfig = RBConfig.init("sdkKey")
                .useRBPlugin(TestRBPlugin.class)
                .useRBPlugin(TestRBPlugin.class)
                .useRBPlugin(TestRBPlugin.class);

        assertEquals("https://api.xenn.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://c.xenn.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.singletonList(TestRBPlugin.class), rbConfig.getRbPlugins());
    }
}