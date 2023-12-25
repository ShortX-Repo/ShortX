package tornaco.apps.shortx.services29.app

import android.app.IUidObserver
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.os.UidObserverCompat

@DoNotStrip
open class UidObserverV29(private val compat: UidObserverCompat) : IUidObserver.Stub() {
    override fun onUidGone(uid: Int, disabled: Boolean) {
        compat.onUidGone(uid)
    }

    override fun onUidActive(uid: Int) {
        compat.onUidActive(uid)
    }

    override fun onUidIdle(uid: Int, disabled: Boolean) {
        compat.onUidIdle(uid)
    }

    override fun onUidStateChanged(uid: Int, procState: Int, procStateSeq: Long) {
        compat.onUidStateChanged(uid, procState)
    }

    override fun onUidCachedChanged(uid: Int, cached: Boolean) {
    }

}