package androidx.test.uiautomator.imp.adapter;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;
import android.view.InputEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface IUiAutomation {

    List<AccessibilityWindowInfo> getWindows();

    AccessibilityNodeInfo getRootInActiveWindow();

    ParcelFileDescriptor executeShellCommand(String cmd);

    Bitmap takeScreenshot();

    AccessibilityEvent executeAndWaitForEvent(
            Runnable runnable,
            UiAutomation.AccessibilityEventFilter filter,
            long timeout) throws TimeoutException;

    AccessibilityServiceInfo getServiceInfo();

    void setServiceInfo(AccessibilityServiceInfo info);

    boolean injectInputEvent(InputEvent event, boolean sync);

    boolean performGlobalAction(int action);

    void setRotation(int rotation);

    void setOnAccessibilityEventListener(UiAutomation.OnAccessibilityEventListener listener);

    void waitForIdle(long quietTimeToBeConsideredIdleState, long timeout) throws TimeoutException;
}
