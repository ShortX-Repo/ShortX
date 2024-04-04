package tornaco.apps.shortx.ui.shortcut

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lang3.RandomStringUtils
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.shortXManager

data class ShortcutStubManageState(
    val shortcutApps: List<App> = emptyList()
)

class ShortcutStubManageViewModel : ViewModel(),
    ContainerHost<ShortcutStubManageState, ApkInstallState> {
    override val container: Container<ShortcutStubManageState, ApkInstallState> =
        container(initialState = ShortcutStubManageState())

    fun loadApps() {
        intent {
            val apps = shortXManager.getInstalledShortcutApps()
            reduce {
                state.copy(shortcutApps = apps)
            }
        }
    }

    fun installShortcutApk(context: Context, info: ShortcutStubInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                doInstallShortcutApk(context, info)
            }
        }
    }

    private fun doInstallShortcutApk(context: Context, info: ShortcutStubInfo) {
        ShortcutStubInstaller.doInstallShortcutApk(
            context,
            info,
            effect = {
                intent {
                    postSideEffect(it)
                }
            }
        )
    }

    companion object {
        fun genPackageName(): String {
            return "shortx.stub.${RandomStringUtils.randomAlphabetic(5).lowercase()}"
        }
    }
}