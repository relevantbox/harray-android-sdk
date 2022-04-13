package io.relevantbox.android.context;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import io.relevantbox.android.model.TestRBPlugin;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class RBPluginRegistryTest {

    @Test
    public void it_should_get_plugin_from_plugin_registry() {
        RBPluginRegistry reg = new RBPluginRegistry();

        reg.initAll(Collections.<Class<? extends RBPlugin>>singletonList(TestRBPlugin.class));

        TestRBPlugin testXennPlugin = reg.get(TestRBPlugin.class);

        Assert.assertTrue(testXennPlugin instanceof TestRBPlugin);
    }

    @Test
    public void it_should_fail_when_init_plugin_registry_with_plugin_that_has_not_no_arg_constructor() {
        RBPluginRegistry reg = new RBPluginRegistry();
        try {
            reg.initAll(Collections.<Class<? extends RBPlugin>>singletonList(MissingNoArgCtor.class));
            fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Default no-arg constructor must be exists on the xenn plugin", e.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void it_should_fail_when_init_plugin_registry_with_plugin_that_with_failed_constructor() {
        RBPluginRegistry reg = new RBPluginRegistry();
        try {
            reg.initAll(Collections.<Class<? extends RBPlugin>>singletonList(FailedCtor.class));
            fail();
        } catch (Exception e) {
            Assert.assertEquals("Plugin initialization error", e.getMessage());
        }
    }

    public static class MissingNoArgCtor extends RBPlugin {

        public MissingNoArgCtor(String value) {
        }
    }

    public static class FailedCtor extends RBPlugin {

        public FailedCtor() {
            throw new RuntimeException("plugin init fail");
        }
    }
}