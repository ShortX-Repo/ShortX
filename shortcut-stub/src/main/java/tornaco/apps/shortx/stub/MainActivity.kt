package tornaco.apps.shortx.stub

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tornaco.apps.shortx.core.ShortXManagerNative

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching {
            ShortXManagerNative.shortX?.shortcutAppLaunched(packageName)
        }.onFailure {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
            ShortXManagerNative.shortX?.log("shortcut.stub ${it.stackTraceToString()}")
        }
        finish()
    }
}