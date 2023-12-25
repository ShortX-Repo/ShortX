package tornaco.apps.shortx.core.os

interface UidObserverCompat {
    fun onUidIdle(uid: Int) {}
    fun onUidGone(uid: Int) {}
    fun onUidActive(uid: Int) {}
    fun onUidStateChanged(uid: Int, procState: Int) {}
}
