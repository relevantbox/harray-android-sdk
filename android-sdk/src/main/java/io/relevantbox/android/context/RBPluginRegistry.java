package io.relevantbox.android.context;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RBPluginRegistry {

    private Map<Class<? extends RBPlugin>, RBPlugin> pluginMap = new HashMap<>();

    public RBPluginRegistry() {
    }

    public <T extends RBPlugin> T get(Class<T> type) {
        try {
            return type.cast(pluginMap.get(type));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("A xenn plugin must 'extends' XennPlugin class", e);
        }
    }

    public void initAll(List<Class<? extends RBPlugin>> xennPlugins) {
        for (Class<? extends RBPlugin> xennPlugin : xennPlugins) {
            try {
                pluginMap.put(xennPlugin, xennPlugin.getConstructor().newInstance());
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Default no-arg constructor must be exists on the xenn plugin", e);
            } catch (Exception e) {
                throw new IllegalArgumentException("Plugin initialization error", e);
            }
        }
    }

    public void onCreate(Context context) {
        for (Map.Entry<Class<? extends RBPlugin>, RBPlugin> entry : pluginMap.entrySet()) {
            entry.getValue().onCreate(context);
        }
    }

    public void onLogin() {
        for (Map.Entry<Class<? extends RBPlugin>, RBPlugin> entry : pluginMap.entrySet()) {
            entry.getValue().onLogin();
        }
    }

    public void onLogout() {
        for (Map.Entry<Class<? extends RBPlugin>, RBPlugin> entry : pluginMap.entrySet()) {
            entry.getValue().onLogout();
        }
    }
}