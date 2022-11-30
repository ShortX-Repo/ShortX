package tornaco.apps.shortx.services

import android.content.ComponentName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.UserComponent
import tornaco.apps.shortx.core.unknownUserComponent

class AppStateService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val _currentFgComponentName =
        MutableStateFlow(unknownUserComponent)
    val currentFgComponentName = _currentFgComponentName.asStateFlow()

    override fun systemReady() {
        super.systemReady()
    }

    fun onComponentMoveToBg(componentName: ComponentName, userId: Int) {
        logger.d("onComponentMoveToBg: $componentName user: $userId")
    }

    fun onComponentMoveToFg(componentName: ComponentName, userId: Int) {
        logger.d("onComponentMoveToFg: $componentName user: $userId")
        _currentFgComponentName.value =
            UserComponent(Pkg(componentName.packageName, userId), componentName.className)
    }
}