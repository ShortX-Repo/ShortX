package tornaco.apps.shortx.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Keep
import tornaco.apps.shortx.core.su.LibSuperuserShellImpl

@Keep
class AppShellService : Service() {
    override fun onBind(intent: Intent): IBinder {
        return LibSuperuserShellImpl
    }
}