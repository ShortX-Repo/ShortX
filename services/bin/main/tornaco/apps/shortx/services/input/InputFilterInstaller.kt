package tornaco.apps.shortx.services.input

import android.view.IInputFilter
import android.view.IInputFilterHost
import android.view.InputEvent
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.android.ServiceHolder
import util.Reflections

class InputFilterInstaller(private val root: ShortXService) {
    private val logger = Logger("InputFilterInstaller")

    fun installInputFilter() {
        val hasDisableGestureFlag =
            root.serviceBinder.hasHookFlags(ShortXManager.Companion.HookFlags.FLAG_DISABLE_GESTURE_HOOK)
        logger.w("installInputFilter, hasDisableGestureFlag: $hasDisableGestureFlag")
        if (!hasDisableGestureFlag) {
            val ims = ServiceHolder.inputManagerService
            logger.w("installInputFilter, ims: $ims")
            if (ims == null) {
                return
            }
            val inputFilter = Reflections.getObjectField(ims, "mInputFilter")
            logger.w("installInputFilter, inputFilter: $inputFilter")

            // Not null means input filter is enabled.
            if (inputFilter == null) {
                Reflections.callMethod(
                    ServiceHolder.inputManagerService,
                    "setInputFilter",
                    InputFilterImpl
                )
                logger.w("installInputFilter, InputFilter installed.")
                Reflections.setObjectField(
                    ServiceHolder.inputManagerService,
                    "mInputFilter",
                    null
                )
                logger.w("installInputFilter, InputFilter removed.")
            }
        } else {
            logger.w("Drop inputFilter set up since hasDisableGestureFlag is true")
        }
        logger.w("installInputFilter, finish.")
    }
}

// abstract method "void android.view.IInputFilter.install(android.view.IInputFilterHost)"
@DoNotStrip
object InputFilterImpl : IInputFilter.Stub() {
    private val logger = Logger("InputFilterImpl")

    override fun install(host: IInputFilterHost) {
        logger.d("install: $host")
    }

    override fun uninstall() {
        logger.d("uninstall")
    }

    override fun filterInputEvent(event: InputEvent, policyFlags: Int) {
        throw IllegalStateException("filterInputEvent should not be called")
    }
}