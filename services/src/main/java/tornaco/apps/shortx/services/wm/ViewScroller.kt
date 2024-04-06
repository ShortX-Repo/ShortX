package tornaco.apps.shortx.services.wm

import android.os.Bundle
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ctrl.PointersState
import tornaco.apps.shortx.services.input.IMSFunc

class ViewScroller(private val windowManagerService: WindowManagerService) {
    companion object {
        const val MAX_VIEW_SCROLL_COUNT = 300
        const val SCROLL_DELAY = 10L
    }

    private val logger = Logger("ShortXWMS")

    private val pointerProperties =
        arrayOfNulls<MotionEvent.PointerProperties>(PointersState.MAX_POINTERS)
    private val pointerCoords = arrayOfNulls<MotionEvent.PointerCoords>(PointersState.MAX_POINTERS)

    init {
        initPointers()
    }

    private fun initPointers() {
        for (i in 0 until PointersState.MAX_POINTERS) {
            val props = MotionEvent.PointerProperties()
            props.toolType = MotionEvent.TOOL_TYPE_FINGER
            val coords = MotionEvent.PointerCoords()
            coords.orientation = 0f
            coords.size = 0f
            pointerProperties[i] = props
            pointerCoords[i] = coords
        }
    }

    suspend fun scrollToForce(hS: Float = 0f, vS: Float = 0f): Boolean {
        logger.d("scrollToForce: $hS $vS")
        val screenSize = windowManagerService.screenSize
        val x = screenSize.x / 2
        val y = screenSize.y / 2

        val now = SystemClock.uptimeMillis()
        val props = pointerProperties[0]
        if (props == null) {
            logger.e("props[0] is null")
            return false
        }

        props.id = 0

        val coords = pointerCoords[0]
        if (coords == null) {
            logger.e("coords[0] is null")
            return false
        }

        coords.x = x.toFloat()
        coords.y = y.toFloat()
        coords.setAxisValue(MotionEvent.AXIS_HSCROLL, hS)
        coords.setAxisValue(MotionEvent.AXIS_VSCROLL, vS)

        val event = MotionEvent
            .obtain(
                now,
                now,
                MotionEvent.ACTION_SCROLL,
                1,
                pointerProperties,
                pointerCoords,
                0,
                0,
                1f,
                1f,
                0,
                0,
                InputDevice.SOURCE_MOUSE,
                0
            )
        logger.d("scrollToForce: $event")

        IMSFunc.injectInputEvent(event)

        return true
    }

    suspend fun scrollToTop(): Boolean {
        logger.d("scrollToTop")
        return performScrollAction { scrollable ->
            val scrollToPositionSuccess =
                scrollable.performAction(
                    AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION.id,
                    Bundle().apply {
                        putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_ROW_INT, 0)
                    })
            if (scrollToPositionSuccess) {
                true
            } else {
                val bounds = scrollable.boundsInScreen
                val centerX = bounds.centerX()
                val centerY = bounds.centerY()
                val endX = centerX
                val endY = centerY + 500
                var loopCount = 0
                var result = false
                while (loopCount < MAX_VIEW_SCROLL_COUNT && scrollable.performAction(
                        AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                    )
                ) {
                    // Inject swipe to make it faster.
                    windowManagerService.swipe(
                        centerX,
                        centerY,
                        endX,
                        endY,
                        SCROLL_DELAY
                    )

                    loopCount += 1
                    result = true
                    delay(SCROLL_DELAY)
                }
                result
            }
        }
    }


    suspend fun scrollToBottom(): Boolean {
        logger.d("scrollToBottom")
        return performScrollAction { scrollable ->
            val scrollToPositionSuccess =
                scrollable.performAction(
                    AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION.id,
                    Bundle().apply {
                        putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_ROW_INT, Int.MAX_VALUE)
                    })
            if (scrollToPositionSuccess) {
                true
            } else {
                val bounds = scrollable.boundsInScreen
                val centerX = bounds.centerX()
                val centerY = bounds.centerY()
                val endX = centerX
                val endY = centerY - 500
                var loopCount = 0
                var result = false
                while (loopCount < MAX_VIEW_SCROLL_COUNT && scrollable.performAction(
                        AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                    )
                ) {
                    // Inject swipe to make it faster.
                    windowManagerService.swipe(
                        centerX,
                        centerY,
                        endX,
                        endY,
                        SCROLL_DELAY
                    )

                    loopCount += 1
                    result = true
                    delay(SCROLL_DELAY)
                }
                result
            }
        }
    }

    // Down
    suspend fun scrollForward(): Boolean =
        performScrollAction {
            it.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
        }

    // Up
    suspend fun scrollBackward(): Boolean =
        performScrollAction {
            it.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
        }

    private suspend fun performScrollAction(action: suspend (AccessibilityNodeInfo) -> Boolean): Boolean {
        val root: AccessibilityNodeInfo? = windowManagerService.automationManager.rootInActiveWindow
        logger.d("performScrollAction, rootInActiveWindow: $root")
        if (root == null) return false

        val scrollableView = findScrollableView(root)
            .sortedWith(compareBy {
                it.className.contains("ViewPager") // 将 "ViewPager" 视为最大值
            })

        onDebugBuild {
            logger.d("performScrollAction, scrollableView: ${scrollableView.joinToString(System.lineSeparator())}")
        }

        if (scrollableView.isEmpty()) return false

        val scrollable: AccessibilityNodeInfo = scrollableView[0]
        val result = action(scrollable)
        root.recycle()

        logger.d("performScrollAction, result: $result")

        return result
    }

    private fun findScrollableView(rootNode: AccessibilityNodeInfo): List<AccessibilityNodeInfo> {
        val list = mutableListOf<AccessibilityNodeInfo>()
        findScrollableViewRecursive(rootNode, list)
        return list
    }

    private fun findScrollableViewRecursive(
        node: AccessibilityNodeInfo,
        list: MutableList<AccessibilityNodeInfo>
    ) {
        if (isScrollableView(node)) {
            list.add(node)
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            child?.let {
                findScrollableViewRecursive(it, list)
            }
        }
    }

    private fun isScrollableView(node: AccessibilityNodeInfo): Boolean {
        return node.isScrollable
    }
}