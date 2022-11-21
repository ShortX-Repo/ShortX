package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.action.*
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.core.gesture.GestureEvent
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.LifeCycleAwareViewModel
import tornaco.apps.shortx.ui.main.actions.getExtraDescription
import javax.inject.Inject

data class MainState(
    val areaSettings: List<AreaSettings> = emptyList(),
    val availableGestureActions: List<String> = emptyList(),
)

data class AreaSettings(
    val area: Area,
    val gestureActions: List<GestureAction>,
    val isEnabled: Boolean,
)

data class GestureAction(
    val gestureEvent: GestureEvent,
    val actions: List<ActionItem>,
)

data class ActionItem(val name: String, val desc: List<String> = emptyList())

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    LifeCycleAwareViewModel() {
    private val shortXManager by lazy { context.shortXManager }

    private val _state = MutableStateFlow(MainState())
    val state get() = _state.asStateFlow()

    private val logger = Logger("MainViewModel")

    fun loadGestureSettings() {
        viewModelScope.launch {
            val left = listOf(Area.LeftTop, Area.LeftCenter, Area.LeftBottom).map { area ->
                AreaSettings(area = area, gestureActions = GestureEvent.values().map {
                    val action = shortXManager.getGestureActions(area, it)
                    GestureAction(it, action.name, action.getExtraDescription())
                }, isEnabled = shortXManager.isGestureEnabledOnArea(area))
            }
            val right = listOf(Area.RightTop, Area.RightCenter, Area.RightBottom).map { area ->
                AreaSettings(area = area, gestureActions = GestureEvent.values().map {
                    val action = shortXManager.getGestureActions(area, it)
                    GestureAction(it, action.name, action.getExtraDescription())
                }, isEnabled = shortXManager.isGestureEnabledOnArea(area))
            }
            val top = listOf(Area.TopLeft, Area.TopCenter, Area.TopRight).map { area ->
                AreaSettings(area = area, gestureActions = GestureEvent.values().map {
                    val action = shortXManager.getGestureActions(area, it)
                    GestureAction(it, action.name, action.getExtraDescription())
                }, isEnabled = shortXManager.isGestureEnabledOnArea(area))
            }
            val bottom = listOf(Area.BottomLeft, Area.BottomCenter, Area.BottomRight).map { area ->
                AreaSettings(area = area, gestureActions = GestureEvent.values().map {
                    val action = shortXManager.getGestureActions(area, it)
                    GestureAction(it, action.name, action.getExtraDescription())
                }, isEnabled = shortXManager.isGestureEnabledOnArea(area))
            }

            val availableActions = listOf(
                StartActivity.NAME,
                ExpandNotification.NAME,
                TakeScreenshot.NAME,
                ShellCommand.NAME,
                NoAction.NAME
            )

            val mainState = MainState(areaSettings = left + right + top + bottom,
                availableGestureActions = availableActions)
            _state.value = mainState
        }
    }

    fun setAreaGestureAction(area: Area, gestureEvent: GestureEvent, action: Action) {
        logger.d("setAreaGestureAction: $area $gestureEvent $action")
        viewModelScope.launch {
            shortXManager.setGestureActions(area, gestureEvent, action)
            loadGestureSettings()
        }
    }

    fun setGestureEnabledOnArea(area: Area, enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setGestureEnabledOnArea(area, enable)
            loadGestureSettings()
        }
    }
}