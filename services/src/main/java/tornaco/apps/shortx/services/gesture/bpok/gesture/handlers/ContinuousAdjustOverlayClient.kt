package tornaco.apps.shortx.services.gesture.bpok.gesture.handlers

import android.content.Context
import tornaco.apps.shortx.core.util.Logger

class ContinuousAdjustOverlayClient(context: Context) {
    private val logger = Logger("AdjustOverlayClient")
    private val controllerClass by lazy {
        runCatching {
            Class.forName(CONTROLLER_CLASS_NAME)
        }.getOrNull()
    }
    private val adjustTypeClass by lazy {
        runCatching {
            Class.forName(ADJUST_TYPE_CLASS_NAME)
        }.getOrNull()
    }
    private val controller by lazy {
        val clazz = controllerClass ?: return@lazy null
        runCatching {
            clazz.getConstructor(Context::class.java).newInstance(context)
        }.onFailure {
            logger.e(it, "createController")
        }.getOrNull()
    }

    fun showBrightness(initialValue: Int, maxValue: Int) {
        show(ADJUST_TYPE_BRIGHTNESS, initialValue, maxValue)
    }

    fun showVolume(initialValue: Int, maxValue: Int) {
        show(ADJUST_TYPE_VOLUME, initialValue, maxValue)
    }

    fun updateValue(value: Int) {
        val target = controller ?: return
        runCatching {
            target.javaClass.getMethod(
                "updateValue",
                Int::class.javaPrimitiveType
            ).invoke(target, value)
        }.onFailure {
            logger.e(it, "updateValue")
        }
    }

    fun dismiss() {
        val target = controller ?: return
        runCatching {
            target.javaClass.getMethod("dismiss").invoke(target)
        }.onFailure {
            logger.e(it, "dismiss")
        }
    }

    private fun show(typeName: String, initialValue: Int, maxValue: Int) {
        val target = controller ?: return
        val enumValue = resolveAdjustType(typeName) ?: return
        val enumClass = adjustTypeClass ?: return
        runCatching {
            target.javaClass.getMethod(
                "show",
                enumClass,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            ).invoke(target, enumValue, initialValue, maxValue)
        }.onFailure {
            logger.e(it, "show:$typeName")
        }
    }

    private fun resolveAdjustType(typeName: String): Any? {
        val clazz = adjustTypeClass ?: return null
        return runCatching {
            clazz.enumConstants.firstOrNull { constant ->
                (constant as? Enum<*>)?.name == typeName
            }
        }.onFailure {
            logger.e(it, "resolveAdjustType:$typeName")
        }.getOrNull()
    }

    private companion object {
        private const val CONTROLLER_CLASS_NAME =
            "tornaco.apps.shortx.ui.overlay.ContinuousAdjustOverlayController"
        private const val ADJUST_TYPE_CLASS_NAME =
            "tornaco.apps.shortx.ui.overlay.ContinuousAdjustOverlayView\$AdjustType"
        private const val ADJUST_TYPE_BRIGHTNESS = "BRIGHTNESS"
        private const val ADJUST_TYPE_VOLUME = "VOLUME"
    }
}