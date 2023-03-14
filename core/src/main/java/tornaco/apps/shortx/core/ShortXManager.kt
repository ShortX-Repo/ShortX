package tornaco.apps.shortx.core

import android.content.Context
import android.content.pm.ParceledListSlice
import android.graphics.Bitmap
import android.os.Bundle
import android.os.ServiceManager
import com.android.internal.appwidget.IAppWidgetService
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.settings.defaultDanmuUISettings
import tornaco.apps.shortx.core.util.Logger
import java.lang.reflect.Method
import java.lang.reflect.Proxy

const val PROXY_SERVICE_NAME = Context.PRINT_SERVICE
val IPC_CODE = "tornaco.apps.shortx.services.SHORTX_SERVER".hashCode()

private val defaultServiceStub = IShortX.Default()
private val logger = Logger("ShortXManager")

val shortXManager by lazy {
    ShortXManager(
        (ShortXManagerNative.shortX
            ?: Proxy.newProxyInstance(
                IShortX::class.java.classLoader,
                arrayOf(IShortX::class.java),
                fun(_: Any, method: Method, args: Array<out Any>): Any? {
                    logger.w("Invoke defaultServiceStub: ${method.name}")
                    return method.invoke(defaultServiceStub, args)
                }
            )) as IShortX
    )
}

class ShortXManager(private val service: IShortX) {
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

    fun isLogDebugEnable(): Boolean {
        return invokeService(false) {
            isLogDebugEnable
        }
    }

    fun setLogDebugEnable(enable: Boolean) {
        return invokeService(Unit) {
            isLogDebugEnable = enable
        }
    }

    fun addRule(rule: Rule) {
        invokeService(Unit) {
            addRule(ByteArrayWrapper(rule.toByteArray()))
        }
    }

    fun deleteRule(ruleId: String) {
        invokeService(Unit) {
            deleteRule(ruleId)
        }
    }

    fun getRuleById(id: String): Rule? {
        return invokeService(null) {
            getRuleById(id)?.byte?.let {
                Rule.parseFrom(it)
            }
        }
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        invokeService(Unit) {
            setRuleEnabled(ruleId, enable)
        }
    }

    fun getAllRules(): List<Rule> {
        return invokeService(emptyList()) {
            allRules.map {
                Rule.parseFrom(it.byte)
            }
        }
    }

    fun getInstalledApps(): List<App> {
        return invokeService(emptyList()) {
            installedApps.map {
                App.parseFrom(it.byte)
            }
        }
    }

    fun getAppLabel(pkg: AppPkg): String {
        return invokeService("Unknown") {
            getAppLabel(ByteArrayWrapper(pkg.toByteArray())) ?: "Unknown"
        }
    }

    fun pkgToApp(pkg: AppPkg): App? {
        return invokeService(null) {
            pkgToApp(ByteArrayWrapper(pkg.toByteArray()))?.let {
                App.parseFrom(it.byte)
            }
        }
    }

    fun showEdgeHint(edge: Edge) {
        invokeService(Unit) {
            showEdgeHint(edge.number)
        }
    }

    fun addDirectAction(da: DirectAction) {
        invokeService(Unit) {
            addDirectAction(ByteArrayWrapper(da.toByteArray()))
        }
    }

    fun deleteDirectAction(id: String) {
        invokeService(Unit) {
            deleteDirectAction(id)
        }
    }

    fun getDirectActionById(id: String): DirectAction? {
        return invokeService(null) {
            getDirectActionById(id)?.byte.let {
                DirectAction.parseFrom(it)
            }
        }
    }

    fun getAllDirectAction(): List<DirectAction> {
        return invokeService(emptyList()) {
            allDirectAction.map {
                DirectAction.parseFrom(it.byte)
            }
        }
    }

    fun executeDirectionActionById(id: String) {
        invokeService(Unit) {
            executeDirectionActionById(id)
        }
    }

    fun addPkgSet(pkgSet: PkgSet) {
        invokeService(Unit) {
            addPkgSet(ByteArrayWrapper(pkgSet.toByteArray()))
        }
    }

    fun deletePkgSet(pkgSetId: String) {
        invokeService(Unit) {
            deletePkgSet(pkgSetId)
        }
    }

    fun getPkgSetById(id: String): PkgSet? {
        return invokeService(null) {
            getPkgSetById(id)?.let {
                PkgSet.parseFrom(it.byte)
            }
        }
    }

    fun getAllPkgSets(withPkgList: Boolean = true): List<PkgSet> {
        return invokeService(emptyList()) {
            getAllPkgSets(withPkgList).map {
                PkgSet.parseFrom(it.byte)
            }
        }
    }


    fun showDanmu(text: String, icon: String?) {
        invokeService(Unit) {
            showDanmu(text, icon)
        }
    }

    fun getDanmuUISettings(): DanmuUISettings {
        return invokeService(defaultDanmuUISettings) {
            DanmuUISettings.parseFrom(danmuUISettings.byte)
        }
    }

    fun setDanmuUISettings(settings: DanmuUISettings) {
        invokeService(Unit) {
            danmuUISettings = ByteArrayWrapper(settings.toByteArray())
        }
    }

    fun getAppIcon(pkg: AppPkg): Bitmap? {
        return invokeService(null) {
            getAppIcon(pkg.pkgName, pkg.userId)
        }
    }

    fun getAppIcon(pkgName: String, userId: Int): Bitmap? {
        return invokeService(null) {
            getAppIcon(pkgName, userId)
        }
    }

    fun getActiveJobs(): List<String> {
        return invokeService(emptyList()) {
            activeJobs
        }
    }

    fun cancelJobs(jobs: List<String>) {
        invokeService(Unit) {
            cancelJobs(jobs)
        }
    }

    fun registerJobStatusListener(listener: IJobStatusListener) {
        invokeService(Unit) {
            registerJobStatusListener(listener)
        }
    }

    fun unregisterJobStatusListener(listener: IJobStatusListener) {
        invokeService(Unit) {
            unregisterJobStatusListener(listener)
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

    private inline fun <T> invokeService(
        defaultValue: T,
        vararg logTokens: Any,
        onService: IShortX.() -> T,
    ): T {
        return kotlin.runCatching {
            onService(service)
        }.getOrElse {
            logger.e(it, "invokeService error, arg: ${logTokens.joinToString(", ")}")
            defaultValue
        }
    }
}

object ShortXManagerNative {
    private val logger = Logger("ShortXManagerNative")

    // IAppWidgetService
    private val androidService
        get() = IAppWidgetService.Stub.asInterface(
            ServiceManager.getService(
                Context.APPWIDGET_SERVICE
            )
        )

    val shortX: IShortX? get() = getServiceOrNull2()

    private fun getServiceOrNull2(): IShortX? {
        return kotlin.runCatching {
            val print = androidService as IAppWidgetService
            val list: ParceledListSlice<*> =
                print.startListening(null, "shortx", 6, intArrayOf(2, 0, 2, 3))
            logger.d("shortx list: $list")
            val bundle = list.list[0] as Bundle
            val shortx = bundle.getBinder("shortX-Binder")
            logger.d("shortx binder: $shortx")
            IShortX.Stub.asInterface(shortx)
        }.getOrElse {
            logger.e(it)
            null
        }
    }
}