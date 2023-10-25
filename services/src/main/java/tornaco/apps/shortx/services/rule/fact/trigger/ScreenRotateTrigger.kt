package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.view.OrientationEventListener
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.condition.RequireScreenRotate
import tornaco.apps.shortx.core.proto.containsAnyTypeUrl
import tornaco.apps.shortx.core.proto.fact.ScreenRotate
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

class ScreenRotateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("ScreenRotateTrigger")
    private val typrUrls by lazy {
        listOf(
            ScreenRotate.getDefaultInstance().pack_().typeUrl,
            RequireScreenRotate.getDefaultInstance().pack_().typeUrl,
        )
    }

    private val listener by lazy {
        object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                shortXContext.wm.currentScreenOrientation = orientation
                val degree = orientation.orientationToDegree()
                logger.d("onOrientationChanged: $orientation $degree")

                shortXContext.rule.publishFact(
                    reason = "onOrientationChanged",
                    fact = ScreenRotate.newBuilder().setDegree(degree).build()
                ) { emptyMap() }
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

        val shouldEnable = shortXContext.rule.getAllEnabledRules().any {
            it.containsAnyTypeUrl(typrUrls)
        } || shortXContext.rule.getAllDirectAction().any {
            it.containsAnyTypeUrl(typrUrls)
        }

        logger.i("updateEnableState, shouldEnable: $shouldEnable")

        if (shouldEnable) {
            listener.enable()
        } else {
            listener.disable()
        }

        logger.i("updateEnableState take: ${System.currentTimeMillis() - startTime}ms")
    }
}

fun Int.orientationToDegree(): ScreenRotateDegree {
    val degree = when (this) {
        0 -> {
            ScreenRotateDegree.ScreenRotateDegree_0
        }

        90 -> {
            ScreenRotateDegree.ScreenRotateDegree_90
        }

        180 -> {
            ScreenRotateDegree.ScreenRotateDegree_180
        }

        270 -> {
            ScreenRotateDegree.ScreenRotateDegree_270
        }

        else -> {
            ScreenRotateDegree.UNRECOGNIZED
        }
    }
    return degree
}