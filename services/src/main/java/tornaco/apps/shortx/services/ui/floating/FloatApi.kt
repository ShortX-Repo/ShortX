package tornaco.apps.shortx.services.ui.floating

import android.content.Context
import tornaco.apps.shortx.services.ui.floating.controller.FloatButtonWindowController
import tornaco.apps.shortx.services.ui.floating.controller.FloatPanelWindowController

class FloatApi(private val context: Context) {

    private lateinit var floatButtonVC: FloatButtonWindowController
    private lateinit var floatPanelVC: FloatPanelWindowController

    private var isFirst = true

    fun showFloatWindow() {
        if (isFirst) {
            val floatWindowManager = FloatWindowManager(context)
            // 填充和浮动面板浮动按钮
            floatPanelVC = FloatPanelWindowController(context, floatWindowManager)
            floatButtonVC = FloatButtonWindowController(context, floatWindowManager, floatPanelVC)
            floatButtonVC.setOnFloatButtonPositionUpdateListener(object :
                FloatButtonWindowController.OnFloatButtonPositionUpdateListener {
                override fun onFloatButtonPositionUpdate(newX: Int, newY: Int) {
                    floatPanelVC.followButtonPosition(
                        newX,
                        newY
                    )
                }
            })
            floatPanelVC.setOnPanelSizeChangeCallback { newWidth, newHeight ->
                // 使用该监听，主要是为了解决第一次进入时，没有手动移动过悬浮球，控制面板没有跟随位置的问题
                val buttonX: Int = floatButtonVC.floatWindow.x
                val buttonY: Int = floatButtonVC.floatWindow.y
                floatPanelVC.followButtonPosition(buttonX, buttonY)
            }
            floatButtonVC.setOnStatusChangeListener(object :
                FloatButtonWindowController.OnStatusChangeListener {
                override fun onPrepareStatusChange(prepareStatus: Int): Boolean {
                    return floatPanelVC.isCanChangeStatus
                }

                override fun onStatusChange(newStatus: Int) {
                }
            })

            val floatViewLiveData: FloatViewLiveData = FloatServiceUtil.floatViewLiveData
            floatViewLiveData.addOnDataChangeCallback { isOpen ->
                // 这里统一做UI切换
                if (isOpen) {
                    floatButtonVC.open()
                    floatPanelVC.open()

                } else {
                    floatButtonVC.off()
                    floatPanelVC.off()

                }
            }
            isFirst = false
        } else {
            floatButtonVC.showFloatWindow()
        }
    }

    private fun hideFloatWindow() {
        floatButtonVC.hideFloatWindow()
    }
}