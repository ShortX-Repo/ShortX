package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import android.os.Binder
import android.os.Parcel
import android.os.ServiceManager
import tornaco.apps.shortx.core.*
import tornaco.apps.shortx.util.BinderProxy

@SuppressLint("StaticFieldLeak")
object ShortXService : SystemService() {
    private val edgeGestureService = EdgeGestureService()

    private val internalServices = listOf(edgeGestureService)

    override fun start(context: Context) {
        super.start(context)
        internalServices.forEach { it.start(context) }
    }

    override fun systemReady() {
        super.systemReady()
        publishBinderService()
        internalServices.forEach { it.systemReady() }
    }

    private fun publishBinderService() {
        ServiceManager.waitForService(PROXY_SERVICE_NAME)?.let { orig ->
            logger.w("publishBinderService, originService: $orig")
            kotlin.runCatching {
                ServiceManager.addService(PROXY_SERVICE_NAME, newBinderProxy(orig as Binder))
                logger.w("publishBinderService, add success.")
            }.onFailure { error ->
                logger.e(error, "Fail to addService")
            }
        }
    }

    private fun newBinderProxy(orig: Binder): BinderProxy {
        return BinderProxy(orig,
            object : BinderProxy.InvocationHandler {
                override fun onTransact(
                    code: Int,
                    data: Parcel?,
                    reply: Parcel,
                    flags: Int,
                ): Boolean? {
                    logger.d("BinderProxy, onTransact: $code")
                    if (code == IPC_CODE) {
                        data!!.enforceInterface(IShortX::class.java.name)
                        val service = serviceBinder
                        reply.writeStrongBinder(service.asBinder())
                        return true
                    }
                    return null
                }
            })
    }

    private val serviceBinder = object : IShortX.Stub() {
        override fun version(): Version {
            return Version(
                name = BuildProp.APP_VERSION_NAME,
                code = BuildProp.APP_VERSION_CODE
            )
        }

        override fun fingerprint() = BuildProp.BUILD_FINGERPRINT

        override fun isEdgeGestureEnabled() = edgeGestureService.isEnabled()

        override fun setEdgeGestureEnabled(enable: Boolean) {
            edgeGestureService.setEnabled(enable)
        }
    }
}