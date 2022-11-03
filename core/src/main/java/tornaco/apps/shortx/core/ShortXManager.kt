package tornaco.apps.shortx.core

import android.content.Context
import android.os.Parcel
import android.os.RemoteException
import android.os.ServiceManager
import tornaco.apps.shortx.core.util.Logger

const val PROXY_SERVICE_NAME = Context.PRINT_SERVICE
val IPC_CODE = "tornaco.apps.shortx.services.SHORTX_SERVER".hashCode()

class ShortXManager {
    private val service: IShortX? by lazy {
        ShortXManagerNative.service
    }

    fun isInstalled() = service != null

    fun version() = service?.version() ?: Version("", 0)

    fun fingerprint() = service?.fingerprint() ?: ""
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