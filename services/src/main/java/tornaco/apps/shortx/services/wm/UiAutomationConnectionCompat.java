package tornaco.apps.shortx.services.wm;

import android.app.UiAutomationConnection;
import android.view.InputEvent;

import com.elvishew.xlog.XLog;

import tornaco.apps.shortx.core.util.OsUtils;
import util.Reflections;

public class UiAutomationConnectionCompat {
    public static boolean injectInputEvent(UiAutomationConnection connection,
                                           InputEvent event,
                                           boolean sync) {
        if (OsUtils.isSOrAbove()) {
            XLog.i("UiAutomationConnectionCompat.injectInputEvent UiAutomationConnectionV31");
            // connection.injectInputEvent(event, sync, waitForAnimations);
            return (boolean) Reflections.callMethod(connection, "injectInputEvent", event, sync, false);
        } else {
            XLog.i("UiAutomationConnectionCompat.injectInputEvent");
            // connection.injectInputEvent(event, sync);
            return (boolean) Reflections.callMethod(connection, "injectInputEvent", event, sync);
        }
    }
}
