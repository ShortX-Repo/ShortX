package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import android.os.Binder
import android.os.Parcel
import android.os.ServiceManager
import tornaco.apps.shortx.core.*
import tornaco.apps.shortx.util.BinderProxy

private typealias ShortXServiceManager = tornaco.apps.shortx.services.ServiceManager

@SuppressLint("StaticFieldLeak")
object ShortXService : SystemService() {
    override fun start(context: Context) {
        super.start(context)
        ShortXServiceManager.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        publishBinderService()
        ShortXServiceManager.systemReady()
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

        override fun isEdgeGestureEnabled() = ShortXServiceManager.gesture.isEnabled()

        override fun setEdgeGestureEnabled(enable: Boolean) {
            ShortXServiceManager.gesture.setEnabled(enable)
        }

        override fun isEdgeGestureEnabledOnArea(areaName: String) =
            ShortXServiceManager.gesture.isEdgeGestureEnabledOnArea(areaName)


        override fun setEdgeGestureEnabledOnArea(areaName: String, enable: Boolean) {
            ShortXServiceManager.gesture.setEdgeGestureEnabledOnArea(areaName, enable)
        }
    }
}