package io.relevantbox.android.common;

import org.junit.Test;

import java.util.Collections;

import io.relevantbox.android.model.TestRBPlugin;

import static org.junit.Assert.assertEquals;

public class RBConfigTest {

    @Test
    public void it_should_build_rbConfig_with_mandatory_fields() {
        RBConfig rbConfig = RBConfig.init("sdkKey");

        assertEquals("https://api.relevantbox.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://c.relevantbox.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.emptyList(), rbConfig.getRbPlugins());
    }

    @Test
    public void it_should_build_rbConfig_with_custom_apiUrl() {
        RBConfig rbConfig = RBConfig.init("sdkKey")
                .apiUrl("https://custom.relevantbox.io:443")
                .collectorUrl("https://customcollector.relevantbox.io:443");

        assertEquals("https://custom.relevantbox.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://customcollector.relevantbox.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.emptyList(), rbConfig.getRbPlugins());
    }

    @Test
    public void it_should_build_rbConfig_with_rbPlugin() {
        RBConfig rbConfig = RBConfig.init("sdkKey")
                .useRBPlugin(TestRBPlugin.class);

        assertEquals("https://api.relevantbox.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://c.relevantbox.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.singletonList(TestRBPlugin.class), rbConfig.getRbPlugins());
    }

    @Test
    public void it_should_build_rbConfig_without_rbPlugin_when_plugin_is_null() {
        RBConfig rbConfig = RBConfig.init("sdkKey")
                .useRBPlugin(null);

        assertEquals("https://api.relevantbox.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://c.relevantbox.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.emptyList(), rbConfig.getRbPlugins());
    }

    @Test
    public void it_should_build_rbConfig_with_only_single_rbPlugin_when_recurring_add() {
        RBConfig rbConfig = RBConfig.init("sdkKey")
                .useRBPlugin(TestRBPlugin.class)
                .useRBPlugin(TestRBPlugin.class)
                .useRBPlugin(TestRBPlugin.class);

        assertEquals("https://api.relevantbox.io:443", rbConfig.getApiUrl());
        assertEquals("sdkKey", rbConfig.getSdkKey());
        assertEquals("https://c.relevantbox.io:443", rbConfig.getCollectorUrl());
        assertEquals(Collections.singletonList(TestRBPlugin.class), rbConfig.getRbPlugins());
    }
}