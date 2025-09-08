package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.graphics.RectF
import tornaco.apps.shortx.core.proto.fact.Edge

/**
 * 手势区域抽象接口
 */
interface GestureArea {
    /**
     * 获取手势区域的矩形范围
     * @param isPortrait 是否为竖屏模式
     * @param screenWidth 屏幕宽度
     * @param screenHeight 屏幕高度
     * @param areaSize 区域大小
     * @param areaLengthScale 区域长度缩放比例
     * @return 区域的RectF
     */
    fun getRect(
        isPortrait: Boolean,
        screenWidth: Float,
        screenHeight: Float,
        areaSize: Float,
        areaLengthScale: Float
    ): RectF

    /**
     * 获取区域标识符
     */
    fun getIdentifier(): String
}

/**
 * 预定义边缘区域实现
 */
class PredefinedEdgeArea(private val edge: Edge) : GestureArea {
    override fun getRect(
        isPortrait: Boolean,
        screenWidth: Float,
        screenHeight: Float,
        areaSize: Float,
        areaLengthScale: Float
    ): RectF {
        return edge.getRect(isPortrait, screenWidth, screenHeight, areaSize, areaLengthScale)
    }

    override fun getIdentifier(): String {
        return edge.name
    }
}

/**
 * 自定义矩形区域实现
 */
class CustomGestureArea(
    private val rect: RectF,
    private val identifier: String = "custom"
) : GestureArea {
    override fun getRect(
        isPortrait: Boolean,
        screenWidth: Float,
        screenHeight: Float,
        areaSize: Float,
        areaLengthScale: Float
    ): RectF {
        // 自定义区域可以选择忽略这些参数，直接返回预设的rect
        // 或者根据屏幕参数进行动态调整
        return rect
    }

    override fun getIdentifier(): String {
        return identifier
    }
}