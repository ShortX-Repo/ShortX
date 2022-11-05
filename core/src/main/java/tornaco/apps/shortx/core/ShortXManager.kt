package tornaco.apps.shortx.core

import android.content.Context
import android.os.Parcel
import android.os.RemoteException
import android.os.ServiceManager
import tornaco.apps.shortx.core.action.Action
import tornaco.apps.shortx.core.action.NoAction
import tornaco.apps.shortx.core.action.toActionOrThrow
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.core.gesture.GestureEvent
import tornaco.apps.shortx.core.util.Logger

const val PROXY_SERVICE_NAME = Context.PRINT_SERVICE
val IPC_CODE = "tornaco.apps.shortx.services.SHORTX_SERVER".hashCode()

val Context.shortXManager
    get() = ShortXManager(this, ShortXManagerNative.service ?: IShortX.Default())

class ShortXManager(private val context: Context, private val service: IShortX) {
    private val logger = Logger("ShortXManager")

    fun isInstalled(): Boolean =
        kotlin.runCatching { service.fingerprint() != null }.getOrElse { false }

    fun version(): Version {
        return invokeService(Version("?", 0)) {
            version()
        }
    }

    fun fingerprint(): String {
        return invokeService("?") {
            fingerprint()
        }
    }

    fun isGestureEnabled(): Boolean {
        return invokeService(false) {
            isGestureEnabled
        }
    }

    fun setGestureEnabled(enable: Boolean) {
        invokeService(Unit) {
            isGestureEnabled = enable
        }
    }

    fun isGestureEnabledOnArea(area: Area): Boolean {
        return invokeService(false) {
            isGestureEnabledOnArea(area.name)
        }
    }

    fun setGestureEnabledOnArea(area: Area, enable: Boolean) {
        invokeService(Unit) {
            setGestureEnabledOnArea(area.name, enable)
        }
    }

    fun setGestureAction(area: Area, event: GestureEvent, action: Action) {
        invokeService(Unit) {
            setGestureAction(area.name, event.name, action.toBundle())
        }
    }

    fun getGestureAction(
        area: Area,
        event: GestureEvent,
    ): Action {
        return invokeService(NoAction()) {
            getGestureAction(area.name, event.name).toActionOrThrow()
        }
    }

    private inline fun <T> invokeService(defaultValue: T, onService: IShortX.() -> T): T {
        return kotlin.runCatching {
            onService(service)
        }.getOrElse {
            logger.e(it, "invokeService error")
            defaultValue
        }
    }
}

object ShortXManagerNative {
    private val logger = Logger("ShortXManagerNative")

    val service: IShortX? by lazy {
        getServiceOrNull()
    }

    private fun getServiceOrNull(): IShortX? {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            val proxyBinder = ServiceManager.getService(PROXY_SERVICE_NAME)
            logger.d("proxyBinder: $proxyBinder")
            if (proxyBinder == null) {
                null
            } else {
                data.writeInterfaceToken(IShortX::class.java.name)
                proxyBinder.transact(IPC_CODE, data, reply, 0)
                val binder = reply.readStrongBinder()
                logger.d("Our binder: $binder")
                IShortX.Stub.asInterface(binder)
            }
        } catch (e: RemoteException) {
            logger.e(e, "Get err")
            null
        } finally {
            data.recycle()
            reply.recycle()
        }
    }
}