package  tornaco.apps.shortx.ui.demo

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug

class DemoService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger("DemoService").d("onStartCommand: ${intent?.action} ${intent?.dataString} ${intent.toString()}")
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, "DemoService start: $intent", Toast.LENGTH_LONG)
                .show()
        }

        return super.onStartCommand(intent, flags, startId)
    }
}