package tornaco.apps.shortx.services.ui.floating

import android.content.Context
import tornaco.apps.shortx.services.ui.floating.controller.FloatButtonWindowController
import tornaco.apps.shortx.services.ui.floating.controller.FloatPanelWindowController

class FloatApi(private val context: Context) {

    private lateinit var mFloatButtonVC: FloatButtonWindowController
    private lateinit var mFloatPanelVC: FloatPanelWindowController

    private var isFirst = true

    fun showFloatWindow() {
        if (isFirst) {
            val floatWindowManager = FloatWindowManager(context)
            // 填充和浮动面板浮动按钮
            mFloatPanelVC = FloatPanelWindowController(context, floatWindowManager)
            mFloatButtonVC = FloatButtonWindowController(context, floatWindowManager, mFloatPanelVC)
            mFloatButtonVC.setOnFloatButtonPositionUpdateListener { newX, newY ->
                mFloatPanelVC.followButtonPosition(
                    newX,
                    newY
                )
            }
            mFloatPanelVC.setOnPanelSizeChangeCallback { newWidth, newHeight ->
                // 使用该监听，主要是为了解决第一次进入时，没有手动移动过悬浮球，控制面板没有跟随位置的问题
                val buttonX: Int = mFloatButtonVC.floatWindow.x
                val buttonY: Int = mFloatButtonVC.floatWindow.y
                mFloatPanelVC.followButtonPosition(buttonX, buttonY)
            }
            mFloatButtonVC.setOnStatusChangeListener(object :
                FloatButtonWindowController.OnStatusChangeListener {
                override fun onPrepareStatusChange(prepareStatus: Int): Boolean {
                    return mFloatPanelVC.isCanChangeStatus
                }

                override fun onStatusChange(newStatus: Int) {
                }
            })

            val floatViewLiveData: FloatViewLiveData = FloatServiceUtil.floatViewLiveData
            floatViewLiveData.addOnDataChangeCallback { isOpen ->
                // 这里统一做UI切换
                if (isOpen) {
                    mFloatButtonVC.open()
                    mFloatPanelVC.open()

                } else {
                    mFloatButtonVC.off()
                    mFloatPanelVC.off()

                }
            }
            isFirst = false
        } else {
            mFloatButtonVC.showFloatWindow()
        }
    }

    private fun hideFloatWindow() {
        mFloatButtonVC.hideFloatWindow()
    }
}