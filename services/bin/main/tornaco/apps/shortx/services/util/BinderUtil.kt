package tornaco.apps.shortx.services.util

import android.os.IInterface
import tornaco.apps.shortx.core.util.Logger

private val logger = Logger("BinderUtil")

fun <T : IInterface> T.callIfAlive(call: (T) -> Unit) {
    if (this.asBinder().isBinderAlive) {
        call(this)
    } else {
        logger.w("Binder die. $this")
    }
}