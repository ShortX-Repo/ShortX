package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.core.gesture.GestureEvent
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.LifeCycleAwareViewModel
import javax.inject.Inject

data class MainState(
    val areaSettings: List<AreaSettings> = emptyList(),
)

data class AreaSettings(val area: Area, val gestureActions: List<GestureAction>)
data class GestureAction(val gestureName: String, val actionName: String)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    LifeCycleAwareViewModel() {
    private val shortXManager by lazy { context.shortXManager }

    private val _state = MutableStateFlow(MainState())
    val state get() = _state.asStateFlow()

    fun loadGestureSettings() {
        viewModelScope.launch {
            val left = listOf(Area.LeftTop, Area.LeftCenter, Area.LeftBottom).map { area ->
                AreaSettings(area = area, gestureActions = GestureEvent.values().map {
                    val action = shortXManager.getGestureAction(area, it)
                    GestureAction(it.name, action.name)
                })
            }
            val right = listOf(Area.RightTop, Area.RightCenter, Area.RightBottom).map { area ->
                AreaSettings(area = area, gestureActions = GestureEvent.values().map {
                    val action = shortXManager.getGestureAction(area, it)
                    GestureAction(it.name, action.name)
                })
            }
            val top = listOf(Area.TopLeft, Area.TopCenter, Area.TopRight).map { area ->
                AreaSettings(area = area, gestureActions = GestureEvent.values().map {
                    val action = shortXManager.getGestureAction(area, it)
                    GestureAction(it.name, action.name)
                })
            }
            val bottom = listOf(Area.BottomLeft, Area.BottomCenter, Area.BottomRight).map { area ->
                AreaSettings(area = area, gestureActions = GestureEvent.values().map {
                    val action = shortXManager.getGestureAction(area, it)
                    GestureAction(it.name, action.name)
                })
            }

            val mainState = MainState(areaSettings = left + right + top + bottom)
            _state.value = mainState
        }
    }
}