package tornaco.apps.shortx.services.input

import android.hardware.input.InputManager
import android.view.Display
import android.view.InputChannel
import android.view.InputEvent
import android.view.KeyEvent
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.android.ServiceHolder
import tornaco.apps.shortx.services.ctrl.Device
import util.Reflections
import util.fieldOrNull

object IMSFunc {
    const val FLAG_WAKE = 0x00000001
    const val FLAG_VIRTUAL = 0x00000002

    const val FLAG_INJECTED = 0x01000000
    const val FLAG_TRUSTED = 0x02000000
    const val FLAG_FILTERED = 0x04000000
    const val FLAG_DISABLE_KEY_REPEAT = 0x08000000

    const val FLAG_INTERACTIVE = 0x20000000
    const val FLAG_PASS_TO_USER = 0x40000000

    private const val injectPolicyFlags =
        FLAG_TRUSTED or FLAG_FILTERED or FLAG_INTERACTIVE or FLAG_PASS_TO_USER

    private val logger = Logger("IMSFunc")

    fun monitorInput(
        inputChannelName: String,
        displayId: Int = Display.DEFAULT_DISPLAY
    ): InputChannel? = runCatching {
        val ims = requireNotNull(ServiceHolder.inputManagerService) {
            "inputManagerService is null"
        }
        Reflections.callMethod(
            ims,
            "monitorInput",
            inputChannelName,
            displayId
        ) as InputChannel
    }.getOrElse {
        logger.e(it, "monitorInput")
        null
    }

    fun injectInputEvent(event: InputEvent) = kotlin.runCatching {
        val ims = requireNotNull(ServiceHolder.inputManagerService) {
            "inputManagerService is null"
        }
        val inputHost = ims.fieldOrNull<Any>("mInputFilterHost")
        logger.d("inputHost: $inputHost")
        if (inputHost != null) {
            injectInputEventWithInputHost(event, inputHost)
        } else {
            if (OsUtils.isTOrAbove()) {
                injectInputEvent33(event, ims)
            } else {
                injectInputEvent(event, ims)
            }
        }
    }.onFailure {
        logger.e(it, "injectInputEvent")
    }

    private fun injectInputEventWithInputHost(event: InputEvent, inputHost: Any) {
        logger.d("injectInputEventWithInputHost: $inputHost")
        // public void sendInputEvent(InputEvent event, int policyFlags)
        Reflections.callMethod(inputHost, "sendInputEvent", event, injectPolicyFlags)
    }

    // Below Android13
    private fun injectInputEvent(event: InputEvent, ims: Any) {
        logger.d("injectInputEvent: $ims")
        //   nativeInjectInputEvent(mPtr, event, Display.DEFAULT_DISPLAY, 0, 0,
        //2226                              InputManager.INJECT_INPUT_EVENT_MODE_ASYNC, 0,
        //2227                              policyFlags | WindowManagerPolicy.FLAG_FILTERED);


        // Android 12
        // private static native int nativeInjectInputEvent(long ptr, InputEvent event, int injectorPid, int injectorUid, int syncMode, int timeoutMillis, int policyFlags);

        // Android 11 http://aospxref.com/android-11.0.0_r21/xref/frameworks/base/services/core/java/com/android/server/input/InputManagerService.java
        // private static native int nativeInjectInputEvent(long ptr, InputEvent event, int injectorPid, int injectorUid, int syncMode, int timeoutMillis, int policyFlags);

        // Android 10 http://aospxref.com/android-10.0.0_r47/xref/frameworks/base/services/core/java/com/android/server/input/InputManagerService.java
        // private static native int nativeInjectInputEvent(long ptr, InputEvent event, int injectorPid, int injectorUid, int syncMode, int timeoutMillis, int policyFlags);

        // Android 9
        // private static native int nativeInjectInputEvent(long ptr, InputEvent event, int displayId, int injectorPid, int injectorUid, int syncMode, int timeoutMillis, int policyFlags);

        // Android 8 http://aospxref.com/android-8.1.0_r81/xref/frameworks/base/services/core/java/com/android/server/input/InputManagerService.java
        //  private static native int nativeInjectInputEvent(long ptr, InputEvent event, int displayId, int injectorPid, int injectorUid, int syncMode, int timeoutMillis, int policyFlags);

        val ptr = Reflections.getObjectField(ims, "mPtr")
        val lock = Reflections.getObjectField(ims, "mInputFilterLock")
        val call = {
            if (OsUtils.isQOrAbove()) {
                Reflections.callMethod(
                    ims, "nativeInjectInputEvent",
                    ptr,
                    event,
                    0,
                    0,
                    InputManager.INJECT_INPUT_EVENT_MODE_ASYNC,
                    0,
                    injectPolicyFlags or FLAG_FILTERED
                )
            } else {
                Reflections.callMethod(
                    ims, "nativeInjectInputEvent",
                    ptr,
                    event,
                    Display.DEFAULT_DISPLAY,
                    0,
                    0,
                    InputManager.INJECT_INPUT_EVENT_MODE_ASYNC,
                    0,
                    injectPolicyFlags or FLAG_FILTERED
                )
            }
        }
        if (lock != null) {
            synchronized(lock) {
                call()
            }
        } else {
            call()
        }
    }

    // Android 13+
    private fun injectInputEvent33(event: InputEvent, ims: Any) {
        logger.d("injectInputEvent33: $ims")
        val nat = requireNotNull(ims.fieldOrNull<Any>("mNative")) { "mNative is null." }
        Reflections.callMethod(
            nat,
            "injectInputEvent",
            if (event is KeyEvent) {
                KeyEvent.changeFlags(
                    event,
                    event.flags or KeyEvent.FLAG_IS_ACCESSIBILITY_EVENT
                )
            } else {
                event
            },
            false,
            -1,
            Device.INJECT_MODE_ASYNC,
            5000,
            injectPolicyFlags or FLAG_FILTERED
        )
    }
}