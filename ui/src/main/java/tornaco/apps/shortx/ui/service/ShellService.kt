package tornaco.apps.shortx.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Keep
import com.topjohnwu.superuser.Shell
import tornaco.apps.shortx.core.rule.action.su.ISu

@Keep
class ShellService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        return object : ISu.Stub() {
            override fun exe(command: Array<String>) {
                Shell.su(*command).exec()
            }
        }
    }
}