package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.hardware.Sensor
import android.view.OrientationEventListener
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.condition.RequireScreenRotate
import tornaco.apps.shortx.core.proto.containsAnyTypeUrl
import tornaco.apps.shortx.core.proto.fact.ScreenRotate
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData

class SensorScreenRotateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("SensorScreenRotateTrigger")
    private val typrUrls by lazy {
        listOf(
            ScreenRotate.getDefaultInstance().pack_().typeUrl,
            RequireScreenRotate.getDefaultInstance().pack_().typeUrl,
        )
    }

    private val listener by lazy {
        object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                runCatching {
                    val degree = orientation.orientationToDegree()
                    if (degree != ScreenRotateDegree.UNRECOGNIZED) {
                        logger.d("orientation: $orientation $degree")
                        if (degree != shortXContext.wm.screenRotateDegree) {
                            logger.d("orientation changed: $orientation $degree")
                            shortXContext.wm.screenRotateDegree = degree
                            shortXContext.rule.publishFactChecked(ScreenRotate.getDefaultInstance()) {
                                FactPublishData(
                                    reason = "onOrientationChanged",
                                    fact = ScreenRotate.newBuilder().setDegree(degree).build()
                                )
                            }
                        }
                    }
                }.onFailure {
                    shortXContext.watchDog.onFatalError(it, "onOrientationChanged")
                }
            }
        }
    }

    override fun register() {
        updateEnableState()
    }

    override fun unRegister() {
        listener.disable()
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        updateEnableState()
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        updateEnableState()
    }

    override fun onAddOrUpdateDA(daId: String) {
        super.onAddOrUpdateDA(daId)
        updateEnableState()
    }

    override fun onDeleteDA(daId: String) {
        super.onDeleteDA(daId)
        updateEnableState()
    }

    private fun updateEnableState() {
        if (!listener.canDetectOrientation()) {
            logger.w("canDetectOrientation is false.")
            return
        }
        val startTime = System.currentTimeMillis()

        val rules = shortXContext.rule.getAllEnabledRules().filter {
            it.containsAnyTypeUrl(typrUrls)
        }.map { it.id }

        val das = shortXContext.rule.getAllDirectAction().filter {
            it.containsAnyTypeUrl(typrUrls)
        }.map { it.id }

        val shouldEnable = rules.isNotEmpty() || das.isNotEmpty()

        logger.i("updateEnableState, shouldEnable: $shouldEnable")

        if (shouldEnable) {
            listener.enable()
            shortXContext.watchDog.onSensorUsage(Sensor.TYPE_ACCELEROMETER, rules + das)
        } else {
            listener.disable()
            shortXContext.watchDog.removeSensorUsage(Sensor.TYPE_ACCELEROMETER)
        }

        logger.i("updateEnableState take: ${System.currentTimeMillis() - startTime}ms")
    }
}

fun Int.orientationToDegree(): ScreenRotateDegree {
    val degree = when (this) {
        in -10..10 -> {
            ScreenRotateDegree.ScreenRotateDegree_0
        }

        in 80..100 -> {
            ScreenRotateDegree.ScreenRotateDegree_90
        }

        in 170..190 -> {
            ScreenRotateDegree.ScreenRotateDegree_180
        }

        in 260..280 -> {
            ScreenRotateDegree.ScreenRotateDegree_270
        }

        else -> {
            ScreenRotateDegree.UNRECOGNIZED
        }
    }
    return degree
}