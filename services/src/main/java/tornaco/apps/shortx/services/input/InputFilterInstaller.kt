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

            if (inputFilter == null) {
                // 这里不要直接反射调用 nativeSetInputFilterEnabled。
                // 本次修改的原因是部分厂商 ROM 会把 InputManagerService 包装成子类
                // （例如 SubInputManagerService），导致在具体实例上做 best-match 反射时，
                // nativeSetInputFilterEnabled 的解析失败并抛出 NoSuchMethodError。
                // 改为调用系统公开的 setInputFilter，让框架自己完成后续 native 状态切换，
                // 可以避开厂商实现差异，同时保持现有 hook 对 input filter 状态变更的监听链路。
                Reflections.callMethod(ims, "setInputFilter", InputFilterImpl)
                logger.w("installInputFilter, InputFilter installed.")
                Reflections.setObjectField(ims, "mInputFilter", null)
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