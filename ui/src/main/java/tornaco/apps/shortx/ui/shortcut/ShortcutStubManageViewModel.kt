package tornaco.apps.shortx.ui.shortcut

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.util.APKInstall
import tornaco.apps.shortx.ui.util.ShortcutHelper
import java.io.File
import java.io.IOException
import java.security.SecureRandom

data class ShortcutStubManageState(
    val shortcutApps: List<App> = emptyList()
)

class ShortcutStubManageViewModel :
    StateViewModel<ShortcutStubManageState>({ ShortcutStubManageState() }) {

    fun loadApps() {

    }

    fun installShortcutApk(context: Context, info: ShortcutStubInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                doInstallShortcutApk(context, info)
            }
        }
    }

    private fun doInstallShortcutApk(context: Context, info: ShortcutStubInfo): Boolean {
        val apkFile = ShortcutHelper.createShortcutStubApkFor(
            context,
            info.appLabel,
            info.appIcon,
            "1.0",
            info.appPkgName,
            1
        )

        val onFailure = {
            composeLogger.i("Install failure.")
        }
        val onSuccess = {
            composeLogger.i("Install success.")
        }
        val session =
            APKInstall.startSession(context, info.appPkgName, onFailure, onSuccess)
        val cmd = "adb_pm_install ${apkFile.absolutePath} ${info.appPkgName}"
        if (Shell.cmd(cmd).exec().isSuccess) return true

        try {
            session.install(context, apkFile)
        } catch (e: IOException) {
            composeLogger.e(e, "install")
            return false
        }
        session.waitIntent()?.let {
            composeLogger.i("waitIntent res: $it")
            context.startActivity(it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } ?: return false

        return true
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
        private const val ALPHA = "abcdefghijklmnopqrstuvwxyz"
        private const val ALPHADOTS = "$ALPHA....."

        fun genPackageName(): String {
            val random = SecureRandom()
            val len = 5 + random.nextInt(15)
            val builder = StringBuilder(len)
            var next: Char
            var prev = 0.toChar()
            for (i in 0 until len) {
                next = if (prev == '.' || i == 0 || i == len - 1) {
                    ALPHA[random.nextInt(ALPHA.length)]
                } else {
                    ALPHADOTS[random.nextInt(ALPHADOTS.length)]
                }
                builder.append(next)
                prev = next
            }
            if (!builder.contains('.')) {
                // Pick a random index and set it as dot
                val idx = random.nextInt(len - 2)
                builder[idx + 1] = '.'
            }
            return builder.toString()
        }
    }
}