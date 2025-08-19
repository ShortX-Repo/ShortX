package tornaco.apps.shortx.core.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

fun newHandlerOfNewThread(name: String?): Handler {
    val hr = HandlerThread("SX-$name")
    hr.start()
    return Handler(hr.looper)
}

fun newLooperOfNewThread(name: String?): Looper? {
    val hr = HandlerThread(name)
    hr.start()
    return hr.looper
}