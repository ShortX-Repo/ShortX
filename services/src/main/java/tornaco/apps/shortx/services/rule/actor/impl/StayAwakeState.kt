package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import tornaco.apps.shortx.core.IStayAwakeListener
import tornaco.apps.shortx.core.util.RemoteCallbackListExt

// Shared "keep screen awake" state across StayAwake / StayAwakeV2, used to surface
// a hint on the app home screen and let the user turn it off from there.
object StayAwakeState {
    private val listeners = RemoteCallbackListExt<IStayAwakeListener>()

    val isEnabled: Boolean
        get() = StayAwake.isStayAwake() || StayAwakeV2.isStayAwake()

    fun disable(context: Context) {
        StayAwake.release()
        StayAwakeV2.release(context.contentResolver)
        notifyChanged()
    }

    fun notifyChanged() {
        val enabled = isEnabled
        listeners.broadcast {
            runCatching { it.onChanged(enabled) }
        }
    }

    fun register(obs: IStayAwakeListener) {
        listeners.register(obs)
    }

    fun unregister(obs: IStayAwakeListener) {
        listeners.unregister(obs)
    }
}
