package tornaco.apps.shortx.ui.shortcut

import android.content.Context
import android.content.Intent
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
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
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.util.StubApkGen
import java.io.File

data class ShortcutStubManageState(
    val shortcutApps: List<App> = emptyList()
)

sealed interface ShortcutStubManageEffect {
    data object Installing : ShortcutStubManageEffect
    data object InstallSuccess : ShortcutStubManageEffect
    data class InstallFailure(val message: String) : ShortcutStubManageEffect
}

class ShortcutStubManageViewModel : ViewModel(),
    ContainerHost<ShortcutStubManageState, ShortcutStubManageEffect> {
    private val logger = Logger("ShortcutStubManageViewModel")

    override val container: Container<ShortcutStubManageState, ShortcutStubManageEffect> =
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
        intent {
            postSideEffect(ShortcutStubManageEffect.Installing)
        }

        runCatching {
            val apkFile = StubApkGen.patchApk(context)
            logger.d("apkFile: $apkFile")

            val pfd = ParcelFileDescriptor.open(apkFile, ParcelFileDescriptor.MODE_READ_WRITE)
            shortXManager.installApk(
                info.appPkgName,
                pfd,
                onSuccess = object : ICallback.Stub() {
                    override fun receive(data: ByteArrayWrapper?) {
                        intent {
                            postSideEffect(ShortcutStubManageEffect.InstallSuccess)
                        }
                    }
                },
                onFailure = object : ICallback.Stub() {
                    override fun receive(data: ByteArrayWrapper?) {
                        intent {
                            postSideEffect(ShortcutStubManageEffect.InstallFailure("Install failed."))
                        }
                    }
                })
        }.onFailure {
            logger.e(it, "Install apk")
            intent {
                postSideEffect(
                    ShortcutStubManageEffect.InstallFailure(
                        it.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    private fun viewApkFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "tornaco.apps.shortx", file)
        val shareIntent = Intent(Intent.ACTION_VIEW)
        shareIntent.setDataAndType(uri, "application/vnd.android.package-archive")
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(shareIntent, "ShortX"))
    }


    companion object {
        fun genPackageName(): String {
            return "shortx.stub.${RandomStringUtils.randomAlphabetic(5).lowercase()}"
        }
    }
}