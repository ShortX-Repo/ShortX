package tornaco.apps.shortx.services.gesture.bpok.gesture.handlers

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import com.google.protobuf.Any as ProtoAny
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.action.PieMenuItem
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.isBase64Image
import tornaco.apps.shortx.core.res.remixResName
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.gesture.bpok.gesture.ContinuousActionHandler
import tornaco.apps.shortx.services.ui.pie.PieEdge
import tornaco.apps.shortx.services.ui.pie.PieMenuOverlay
import java.util.UUID

class PieMenuContinuousHandler(
    private val context: Context,
    private val items: List<PieMenuItem>,
    private val pieColor: Int,
    private val iconColor: Int,
    private val scalePercent: Int,
    private val arcSpanDegrees: Float,
    private val ringCount: Int,
    private val labelSizeSp: Int,
) : ContinuousActionHandler {
    private val logger = Logger("PieMenuHandler")
    private val overlay = PieMenuOverlay(context)
    private val overlayScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val appRes by lazy { AppResources(context, BuildPropInternal.APP_PACKAGE_NAME) }

    private var startX = 0f
    private var startY = 0f

    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        startX = x
        startY = y
        val edge = edgeGroupToPieEdge(edgeGroup)
        val drawables = items.map { createItemDrawable(it) }
        val labels = items.map { it.label.orEmpty() }

        overlayScope.launch {
            overlay.show(
                edge = edge,
                x = x,
                y = y,
                itemDrawables = drawables,
                itemLabels = labels,
                pieColor = pieColor,
                iconColor = iconColor,
                scalePercent = scalePercent,
                arcSpanDegrees = arcSpanDegrees,
                ringCount = ringCount,
                labelSizeSp = labelSizeSp,
            )
        }
        logger.d("onStart: edge=$edge, x=$x, y=$y, items=${items.size}")
    }

    override fun onMove(x: Float, y: Float, time: Long) {
        overlayScope.launch {
            overlay.updateSelection(x, y)
        }
    }

    override fun onFinish(x: Float, y: Float, time: Long) {
        overlayScope.launch {
            overlay.updateSelection(x, y)
            val selectedIndex = overlay.getSelectedItemIndex()
            overlay.hide()

            if (selectedIndex in items.indices) {
                val item = items[selectedIndex]
                if (item.hasAction()) {
                    executeAction(item.action)
                }
                logger.d("onFinish: selected=${item.label} (index=$selectedIndex)")
            } else {
                logger.d("onFinish: no selection")
            }
        }
    }

    override fun onCancel() {
        overlayScope.launch {
            overlay.hide()
        }
        logger.d("onCancel")
    }

    private fun executeAction(action: ProtoAny) {
        try {
            val evaluateContext = EvaluateContext.newBuilder()
                .setSrcId("pie-menu-${UUID.randomUUID()}")
                .build()
            shortXManager.executeAction(evaluateContext, action)
        } catch (e: Exception) {
            logger.e(e, "Failed to execute pie action")
        }
    }

    private fun createItemDrawable(item: PieMenuItem): Drawable? {
        val iconDesc = item.iconDescription
        if (iconDesc.isNullOrEmpty()) {
            return null
        }
        return try {
            if (iconDesc.isBase64Image()) {
                val bitmap = BitmapUtils.base64ToBitmap(iconDesc)
                if (bitmap != null) {
                    BitmapDrawable(context.resources, bitmap)
                } else {
                    null
                }
            } else {
                val resName = remixResName(iconDesc)
                if (resName != null) {
                    val drawable = appRes.getDrawable(resName)
                    if (drawable != null && iconColor != 0) {
                        drawable.mutate().apply { setColorFilter(iconColor, PorterDuff.Mode.SRC_IN) }
                    } else {
                        drawable
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            logger.e(e, "Failed to load icon: $iconDesc")
            null
        }
    }

    private fun edgeGroupToPieEdge(edgeGroup: Int): PieEdge {
        return when (edgeGroup) {
            0 -> PieEdge.LEFT
            1 -> PieEdge.RIGHT
            2 -> PieEdge.TOP
            3 -> PieEdge.BOTTOM
            else -> PieEdge.LEFT
        }
    }
}
