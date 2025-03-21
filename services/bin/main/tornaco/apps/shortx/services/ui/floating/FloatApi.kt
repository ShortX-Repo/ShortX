package tornaco.apps.shortx.services.ui.floating

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import tornaco.apps.shortx.core.proto.action.ShowFloatWindow
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.services.ui.floating.controller.FloatButtonWindowController
import tornaco.apps.shortx.services.ui.floating.controller.FloatPanelWindowController

class FloatApi(
    private val context: Context,
    val action: ShowFloatWindow,
    private val initialPosP: Pair<Int, Int>,
    private val initialPosL: Pair<Int, Int>,
    private val onPosChange: (Int, Int) -> Unit,
    private val onButtonClick: (OverlayButtonSetting) -> Unit,
    private val onButtonLongClick: (OverlayButtonSetting) -> Unit,
) {
    private val util = FloatServiceUtil()
    private val floatWindowManager = FloatWindowManager(context)
    private val floatButtonVC by lazy {
        FloatButtonWindowController(context, util, floatWindowManager, floatPanelVC, onPosChange)
    }
    private val floatPanelVC by lazy {
        FloatPanelWindowController(
            context,
            util,
            floatWindowManager,
            action,
            onButtonClick,
            onButtonLongClick
        )
    }

    private var isFirst = true

    fun showFloatWindow() {
        if (isFirst) {
            util.p.x = initialPosP.first
            util.p.y = initialPosP.second
            util.p.px = initialPosP.first
            util.p.py = initialPosP.second

            util.l.x = initialPosL.first
            util.l.y = initialPosL.second
            util.l.px = initialPosL.first
            util.l.py = initialPosL.second

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

            val floatViewLiveData: FloatViewLiveData = util.floatViewLiveData
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

    fun hideFloatWindow() {
        floatButtonVC.hideFloatWindow()
    }

    fun removeFloatWindow() {
        floatButtonVC.removeFloatWindow()
    }

    fun onConfigurationChanged() {
        val currentOrientation = context.resources.configuration.orientation
        Log.d(
            "TORNACO",
            "FloatApi.onConfigurationChanged currentOrientation: $currentOrientation (ORIENTATION_PORTRAIT = 1, ORIENTATION_LANDSCAPE = 2)"
        )
        val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT

        if (isPortrait) {
            floatButtonVC.updateXY(util.p.x, util.p.y)
        } else {
            floatButtonVC.updateXY(util.l.x, util.l.y)
        }
    }
}