package tornaco.apps.shortx.services.wm

import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.util.Logger

class ViewScroller(private val windowManagerService: WindowManagerService) {
    companion object {
        const val MAX_VIEW_SCROLL_COUNT = 300
        const val SCROLL_DELAY = 10L
    }

    private val logger = Logger("ShortXWMS")

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