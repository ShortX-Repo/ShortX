package tornaco.apps.shortx.services.wm;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.os.Handler;

class GestureResultCallbackInfo {
    GestureDescription gestureDescription;
    AccessibilityService.GestureResultCallback callback;
    Handler handler;

    GestureResultCallbackInfo(GestureDescription gestureDescription, AccessibilityService.GestureResultCallback callback, Handler handler) {
    }
}