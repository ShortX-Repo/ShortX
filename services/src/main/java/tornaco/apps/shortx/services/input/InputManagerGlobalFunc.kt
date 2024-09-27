package tornaco.apps.shortx.services.input

import android.content.Context
import android.hardware.input.IInputManager
import android.os.Binder
import android.os.RemoteException
import android.os.ServiceManager
import android.view.InputMonitor
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import util.Reflections

object InputManagerGlobalFunc {
    private const val MONITOR_GESTURE_INPUT = "monitorGestureInput"
    private val logger = Logger("InputManagerGlobalUtil")

    private val mIm by lazy {
        IInputManager.Stub.asInterface(ServiceManager.getService(Context.INPUT_SERVICE))
    }

    fun monitorGestureInput(name: String?, displayId: Int): InputMonitor? {
        try {
            return kotlin.runCatching {
                Reflections.callMethod(mIm, MONITOR_GESTURE_INPUT, Binder(), name, displayId)
            }.getOrElse {
                Reflections.callMethod(mIm, MONITOR_GESTURE_INPUT, name, displayId)
            } as InputMonitor?
        } catch (ex: RemoteException) {
            logger.e(ex, "monitorGestureInput")
            return null
        }
    }
}