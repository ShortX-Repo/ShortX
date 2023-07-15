package tornaco.apps.shortx.services.ops

import android.content.Context
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services29.AppOpsManagerV29
import tornaco.apps.shortx.services30.AppOpsManagerV30

@DoNotStrip
class AppOpsManagerCompat {
    private val logger = Logger("AppOpsManagerCompat")

    @DoNotStrip
    interface OnOpActiveChangedListenerCompat {
        fun onOpActiveChanged(opCode: Int, uid: Int, packageName: String?, active: Boolean)
    }

    fun startWatchingActive(
        context: Context?,
        ops: IntArray?,
        listenerCompat: OnOpActiveChangedListenerCompat
    ) {
        logger.w("AppOpsManagerCompat startWatchingActive")
        if (OsUtils.isQ()) {
            AppOpsManagerV29.startWatchingActive(
                context, ops
            ) { opCode: Int, uid: Int, packageName: String?, active: Boolean ->
                listenerCompat.onOpActiveChanged(
                    opCode,
                    uid,
                    packageName,
                    active
                )
            }
        } else if (OsUtils.isROrAbove()) {
            AppOpsManagerV30.startWatchingActive(
                context, ops
            ) { opCode: Int, uid: Int, packageName: String?, active: Boolean ->
                listenerCompat.onOpActiveChanged(
                    opCode,
                    uid,
                    packageName,
                    active
                )
            }
        }
    }
}