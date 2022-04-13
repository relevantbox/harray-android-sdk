package io.relevantbox.android;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import io.relevantbox.android.common.Constants;
import io.relevantbox.android.common.RBConfig;
import io.relevantbox.android.context.ApplicationContextHolder;
import io.relevantbox.android.context.SessionContextHolder;
import io.relevantbox.android.context.SessionState;
import io.relevantbox.android.context.RBPluginRegistry;
import io.relevantbox.android.event.EventProcessorHandler;
import io.relevantbox.android.event.SDKEventProcessorHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RBTest {

    @Mock
    private Context context;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    @Test
    public void it_should_return_same_instance_when_get_method_called_more_than_one_time() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        RB.configure(context, RBConfig.init("SdkKey"));
        RB rb = RB.getInstance();
        RB rb2 = RB.getInstance();
        assertEquals(rb, rb2);
    }

    @Test(expected = IllegalStateException.class)
    public void it_should_throw_illegal_state_exception_when_configuration_is_not_made() {
        RB.getInstance();
    }

    @Test
    public void it_should_initialized_shared_prefs_with_rb_key() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        RB.configure(context, RBConfig.init("SdkKey"));

        verify(mockSharedPreferences).getString(Constants.SDK_PERSISTENT_ID_KEY, null);
        verify(mockEditor).putString(eq(Constants.SDK_PERSISTENT_ID_KEY), anyString());
        verify(mockEditor).apply();
    }

    @Test
    public void it_should_set_member_id_to_context_when_member_id_is_not_empty() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        RB.configure(context, RBConfig.init("SdkKey"));
        RB.login("memberId");

        RB instance = RB.getInstance();

        assertEquals("memberId", instance.sessionContextHolder.getMemberId());
    }

    @Test
    public void it_should_set_member_id_to_context_when_member_id_is_not_empty_and_trigger_onLogin_method_of_plugins() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        RB.configure(context, RBConfig.init("SdkKey"));

        RB instance = RB.getInstance();
        instance.rbPluginRegistry = mock(RBPluginRegistry.class);

        RB.login("memberId");

        assertEquals("memberId", instance.sessionContextHolder.getMemberId());
        verify(instance.rbPluginRegistry).onLogin();
    }

    @Test
    public void it_should_not_set_member_id_to_context_when_member_id_is_empty() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        RB.configure(context, RBConfig.init("SdkKey"));
        RB.login("");

        RB instance = RB.getInstance();

        assertNull(instance.sessionContextHolder.getMemberId());
    }

    @Test
    public void it_should_not_set_member_id_to_context_when_member_id_is_null() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        RB.configure(context, RBConfig.init("SdkKey"));
        RB.login(null);

        RB instance = RB.getInstance();

        assertNull(instance.sessionContextHolder.getMemberId());
    }

    @Test
    public void it_should_set_null_as_member_id_when_log_out_invoked() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        RB.configure(context, RBConfig.init("SdkKey"));
        RB.login("memberId");
        RB instance = RB.getInstance();
        instance.rbPluginRegistry = mock(RBPluginRegistry.class);
        assertEquals("memberId", instance.sessionContextHolder.getMemberId());

        RB.logout();

        assertNull(instance.sessionContextHolder.getMemberId());
        verify(instance.rbPluginRegistry).onLogout();
    }

    @Test
    public void it_should_call_session_start_and_new_installation_when_eventing_called() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        RB.configure(context, RBConfig.init("SdkKey"));
        RB instance = RB.getInstance();
        instance.applicationContextHolder = mock(ApplicationContextHolder.class);
        instance.sdkEventProcessorHandler = mock(SDKEventProcessorHandler.class);
        instance.sessionContextHolder = mock(SessionContextHolder.class);

        when(instance.sessionContextHolder.getSessionState()).thenReturn(SessionState.SESSION_INITIALIZED);
        when(instance.applicationContextHolder.isNewInstallation()).thenReturn(true);
        RB.eventing();

        verify(instance.sessionContextHolder).startSession();
        verify(instance.sdkEventProcessorHandler).newInstallation();
        verify(instance.applicationContextHolder).setInstallationCompleted();

    }

    @Test
    public void it_should_not_call_session_start_and_new_installation_when_eventing_called_second_time() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        RB.configure(context, RBConfig.init("SdkKey"));
        RB instance = RB.getInstance();
        instance.applicationContextHolder = mock(ApplicationContextHolder.class);
        instance.sdkEventProcessorHandler = mock(SDKEventProcessorHandler.class);
        instance.sessionContextHolder = mock(SessionContextHolder.class);

        when(instance.sessionContextHolder.getSessionState()).thenReturn(SessionState.SESSION_STARTED);
        EventProcessorHandler handler = RB.eventing();

        verify(instance.sessionContextHolder, never()).startSession();
        verifyNoInteractions(instance.sdkEventProcessorHandler);
        verifyNoInteractions(instance.applicationContextHolder);
        assertEquals(handler, instance.eventProcessorHandler);

    }

    @Test
    public void it_should_synchronize_intent_data() {
        when(context.getSharedPreferences(Constants.PREF_COLLECTION_NAME, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        RB.configure(context, RBConfig.init("SdkKey"));
        RB instance = RB.getInstance();
        instance.sessionContextHolder = mock(SessionContextHolder.class);
        Map<String, Object> intent = new HashMap<>();
        RB.synchronizeIntentData(intent);

        verify(instance.sessionContextHolder).updateExternalParameters(intent);
    }
}