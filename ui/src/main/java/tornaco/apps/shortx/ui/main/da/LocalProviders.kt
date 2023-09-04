package tornaco.apps.shortx.ui.main.da

import androidx.compose.runtime.staticCompositionLocalOf
import tornaco.apps.shortx.ui.main.model.DirectActionUM

interface DAItemHandler {
    fun run(da: DirectActionUM) {}
    fun dup(da: DirectActionUM) {}
    fun delete(da: DirectActionUM) {}
    fun edit(da: DirectActionUM) {}
    fun click(da: DirectActionUM) {}
    fun deepLink(da: DirectActionUM) {}
    fun share(da: DirectActionUM) {}
    fun moveToSet(da: DirectActionUM, setId: String) {}
    fun stopAllJobs(da: DirectActionUM) {}
}

interface DASetItemHandler {
    fun delete(da: DirectActionUM) {}
    fun edit(da: DirectActionUM) {}
    fun click(da: DirectActionUM) {}
    fun share(da: DirectActionUM) {}
}

val LocalDAItemHandler = staticCompositionLocalOf<DAItemHandler> {
    error("CompositionLocal LocalDAItemHandler not present")
}
val LocalDASetItemHandler = staticCompositionLocalOf<DASetItemHandler> {
    error("CompositionLocal LocalDASetItemHandler not present")
}