package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import android.os.Binder
import android.os.Parcel
import android.os.ServiceManager
import tornaco.apps.shortx.core.*
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper

@SuppressLint("StaticFieldLeak")
object ShortXService : SystemService(ShortXContextImpl()) {
    private val serviceLifecycle by lazy { ServiceLifecycle(shortXContext) }

    override fun start(context: Context) {
        super.start(context)
        serviceLifecycle.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        publishBinderService()
        serviceLifecycle.systemReady()
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

    private fun newBinderProxy(orig: Binder): tornaco.apps.shortx.services.util.BinderProxy {
        return tornaco.apps.shortx.services.util.BinderProxy(orig,
            object : tornaco.apps.shortx.services.util.BinderProxy.InvocationHandler {
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
            return Version(name = BuildProp.APP_VERSION_NAME, code = BuildProp.APP_VERSION_CODE)
        }

        override fun fingerprint() = BuildProp.BUILD_FINGERPRINT

        override fun addRule(rule: ByteArrayWrapper) {
            shortXContext.rule.addRule(Rule.parseFrom(rule.byte))
        }

        override fun deleteRule(ruleId: String) {
            shortXContext.rule.deleteRule(ruleId)
        }

        override fun setRuleEnabled(ruleId: String, enable: Boolean) {
            shortXContext.rule.setRuleEnabled(ruleId, enable)
        }

        override fun getAllRules(): List<ByteArrayWrapper> {
            return shortXContext.rule.getAllRules().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }
    }
}