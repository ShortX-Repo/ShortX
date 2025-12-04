package tornaco.apps.shortx.core.rule.script.api;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.os.Handler;
import android.view.InputEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.google.protobuf.Message;

import java.util.List;
import java.util.Map;

import tornaco.apps.shortx.core.annotations.DoNotStrip;
import tornaco.apps.shortx.core.proto.da.DirectAction;
import tornaco.apps.shortx.core.proto.rule.Rule;

@DoNotStrip
public interface ScriptShortXApi {

    /**
     * Return String for primitive type.
     * Return {@link List<String>} for List type.
     */
    Object readGlobalVar(String varName);

    void writeGlobalVar(String varName, String varValue);

    /**
     * Op value: one of below op enum numbers, e.g. 3 for override.
     * See {@link tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp}
     */
    void writeGlobalVarWithOp(String varName, String varValue, int op);

    List<String> queryRuleIdByTitleAndDescription(String title, String description);

    List<String> queryDAIdByTitleAndDescription(String title, String description);

    List<String> queryRuleIdByTitle(String title);

    List<String> queryDAIdByTitle(String title);

    Rule queryRuleById(String id);

    DirectAction queryDAById(String id);

    void executeDAById(String id);

    void enableDisableRuleById(String id, boolean enable);

    Result executeAction(Message action);

    Result executeActions(List<Message> actions);

    UiAutomationApi getUiAutomation();

    @DoNotStrip
    class Result {
        /**
         * Context data from the actions.
         */
        public Map<String, Object> contextData;
    }

    @DoNotStrip
    interface UiAutomationApi {
        /**
         * 是否已连接，使用API前必须确保已经连接
         */
        boolean isConnected();

        /**
         * 连接，使用API前必须确保已经连接
         */
        void connect();

        /**
         * 断开连接，使用API前必须确保已经连接
         */
        void disconnect();

        AccessibilityNodeInfo getRootInActiveWindow();

        void performAccessibilityAction(long sourceNodeId, int windowId, int action);

        AccessibilityServiceInfo getServiceInfo();

        boolean injectInputEvent(InputEvent event, boolean sync);

        boolean performGlobalAction(int action);

        boolean dispatchGesture(@NonNull GestureDescription gesture,
                                @Nullable AccessibilityService.GestureResultCallback callback,
                                @Nullable Handler handler);

        AccessibilityNodeInfo findFocus(int focus);

        boolean clearCache();

        List<AccessibilityWindowInfo> getWindows();

        void waitForIdle(long idleTimeoutMillis, long globalTimeoutMillis);

        boolean isCacheEnabled();
    }
}
