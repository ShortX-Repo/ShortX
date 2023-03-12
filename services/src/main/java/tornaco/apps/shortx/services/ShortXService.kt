package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Parcel
import android.os.ServiceManager
import tornaco.apps.shortx.core.*
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.ApkUtils
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.UserHandleUtils.userIdToUserHandle

@SuppressLint("StaticFieldLeak")
object ShortXService : SystemService(ShortXContextImpl()) {
    private val serviceLifecycle by lazy { ServiceLifecycle(shortXContext) }

    init {
        LogInit.initEarlyLogging()
    }

    fun main() {}

    override fun start(context: Context) {
        super.start(context)
        LogInit.initStartedLogging()
        serviceLifecycle.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        //  publishBinderService()
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

    val serviceBinder = object : IShortX.Stub() {
        override fun version(): Version {
            return Version(name = BuildProp.APP_VERSION_NAME, code = BuildProp.APP_VERSION_CODE)
        }

        override fun fingerprint() = BuildProp.BUILD_FINGERPRINT

        override fun setLogDebugEnable(enable: Boolean) {
            LogInit.setLogDebug(enable)
        }

        override fun isLogDebugEnable(): Boolean {
            return LogInit.logDebug
        }

        override fun addRule(rule: ByteArrayWrapper) {
            shortXContext.rule.addRule(Rule.parseFrom(rule.byte))
        }

        override fun deleteRule(ruleId: String) {
            shortXContext.rule.deleteRule(ruleId)
        }

        override fun getRuleById(id: String): ByteArrayWrapper? {
            return shortXContext.rule.getRuleById(id)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun setRuleEnabled(ruleId: String, enable: Boolean) {
            shortXContext.rule.setRuleEnabled(ruleId, enable)
        }

        override fun getAllRules(): List<ByteArrayWrapper> {
            return shortXContext.rule.getAllRules().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getInstalledApps(): List<ByteArrayWrapper> {
            return shortXContext.appState.getInstalledApps().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getAppLabel(pkg: ByteArrayWrapper): String? {
            return shortXContext.appState.getAppLabel(AppPkg.parseFrom(pkg.byte))
        }

        override fun pkgToApp(pkg: ByteArrayWrapper): ByteArrayWrapper? {
            return shortXContext.appState.pkgToApp(AppPkg.parseFrom(pkg.byte))?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun showEdgeHint(edgeNumber: Int) {
            shortXContext.gesture.showEdgeHint(edgeNumber)
        }

        override fun addDirectAction(da: ByteArrayWrapper) {
            shortXContext.rule.addDirectAction(DirectAction.parseFrom(da.byte))
        }

        override fun deleteDirectAction(id: String) {
            shortXContext.rule.deleteDirectAction(id)
        }

        override fun getDirectActionById(id: String): ByteArrayWrapper? {
            return shortXContext.rule.getDirectActionById(id)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getAllDirectAction(): List<ByteArrayWrapper> {
            return shortXContext.rule.getAllDirectAction().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun executeDirectionActionById(id: String) {
            shortXContext.rule.executeDirectionActionById(id)
        }

        override fun addPkgSet(set: ByteArrayWrapper) {
            shortXContext.pkgSet.addPkgSet(PkgSet.parseFrom(set.byte))
        }

        override fun deletePkgSet(id: String) {
            shortXContext.pkgSet.deletePkgSet(id)
        }

        override fun getPkgSetById(id: String): ByteArrayWrapper? {
            return shortXContext.pkgSet.getPkgSetById(id)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getAllPkgSets(withPkgList: Boolean): List<ByteArrayWrapper> {
            return shortXContext.pkgSet.getAllPkgSets().map {
                val trimmed =
                    if (withPkgList) {
                        it
                    } else {
                        PkgSet.newBuilder()
                            .setLabel(it.label)
                            .setId(it.id)
                            .build()
                    }
                ByteArrayWrapper(trimmed.toByteArray())
            }
        }

        override fun showDanmu(text: String, icon: String?) {
            shortXContext.ui.showDanmu(icon = icon, text = text)
        }

        override fun getDanmuUISettings(): ByteArrayWrapper {
            return ByteArrayWrapper(shortXContext.ui.getDanmuUISettings().toByteArray())
        }

        override fun setDanmuUISettings(settings: ByteArrayWrapper) {
            shortXContext.ui.setDanmuUISettings(DanmuUISettings.parseFrom(settings.byte))
        }

        override fun getAppIcon(pkgName: String, userId: Int): Bitmap? {
            return runClearCallingIdentity {
                val drawable: Drawable? = ApkUtils.loadIconByPkgName(context, pkgName)
                drawable?.let {
                    val badged =
                        context.packageManager.getUserBadgedIcon(it, userIdToUserHandle(userId))
                    BitmapUtils.getBitmap(context, badged)
                }
            }
        }
    }
}