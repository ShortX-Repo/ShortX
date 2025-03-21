package tornaco.apps.shortx.services.app

import android.app.ActivityManager
import android.app.IActivityManager
import android.app.IUidObserver
import android.content.Context
import android.os.Build
import android.os.ServiceManager
import tornaco.apps.shortx.core.os.UidObserverCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services28.app.UidObserverV28
import tornaco.apps.shortx.services29.app.UidObserverV29
import tornaco.apps.shortx.services30.app.UidObserverV30
import tornaco.apps.shortx.services31.app.UidObserverV31
import tornaco.apps.shortx.services32.app.UidObserverV32
import tornaco.apps.shortx.services33.app.UidObserverV33
import tornaco.apps.shortx.services34.app.UidObserverV34
import tornaco.apps.shortx.services35.app.UidObserverV35


object UidObserverRegisterCompat {
    private val logger = Logger("UidObserverRegisterCompat")

    fun register(obs: UidObserverCompat) {
        val am =
            IActivityManager.Stub.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE))

        val observer: IUidObserver? = when (Build.VERSION.SDK_INT) {
            28 -> {
                UidObserverV28(obs)
            }

            29 -> {
                UidObserverV29(obs)
            }

            30 -> {
                UidObserverV30(obs)
            }

            31 -> {
                UidObserverV31(obs)
            }

            32 -> {
                UidObserverV32(obs)
            }

            33 -> {
                UidObserverV33(obs)
            }

            34 -> {
                UidObserverV34(obs)
            }

            35 -> {
                UidObserverV35(obs)
            }

            else -> {
                null
            }
        }

        logger.d("$obs = $observer")

        am.registerUidObserver(
            observer,
            ActivityManager.UID_OBSERVER_PROCSTATE or ActivityManager.UID_OBSERVER_GONE
                    or ActivityManager.UID_OBSERVER_IDLE or ActivityManager.UID_OBSERVER_ACTIVE,
            ActivityManager.PROCESS_STATE_UNKNOWN,
            null
        )
    }
}