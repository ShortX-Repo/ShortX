package tornaco.apps.shortx.ui.main.da

import androidx.compose.runtime.staticCompositionLocalOf
import tornaco.apps.shortx.ui.main.model.DirectActionUM

interface DAItemHandler {
    fun run(da: DirectActionUM) {}
    fun dup(da: DirectActionUM) {}
    fun createApk(da: DirectActionUM) {}
    fun delete(da: DirectActionUM) {}
    fun edit(da: DirectActionUM) {}
    fun click(da: DirectActionUM) {}
    fun deepLink(da: DirectActionUM) {}
    fun shellCommand(da: DirectActionUM) {}
    fun share(da: DirectActionUM) {}
    fun moveToSet(da: DirectActionUM, setId: String) {}
    fun moveOutSet(da: DirectActionUM) {}
    fun stopAllJobs(da: DirectActionUM) {}
}

interface DASetItemHandler {
    fun dismiss(da: DirectActionUM) {}
    fun delete(da: DirectActionUM, alsoDeleteItems: Boolean) {}
    fun rename(da: DirectActionUM, newName: String) {}
    fun click(da: DirectActionUM) {}
    fun share(da: DirectActionUM) {}
}

val LocalDAItemHandler = staticCompositionLocalOf<DAItemHandler> {
    error("CompositionLocal LocalDAItemHandler not present")
}
val LocalDASetItemHandler = staticCompositionLocalOf<DASetItemHandler> {
    error("CompositionLocal LocalDASetItemHandler not present")
}