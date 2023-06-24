package tornaco.apps.shortx.core

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ParceledListSlice
import android.graphics.Bitmap
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.ServiceManager
import com.android.internal.appwidget.IAppWidgetService
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.CommonApiRes
import tornaco.apps.shortx.core.proto.common.UserInfo
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.ProtoAny
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
    val isInstalled get() = kotlin.runCatching { service.fingerprint() != null }.getOrElse { false }

    val isFrameworkUpdated
        get() = kotlin.runCatching {
            isInstalled && service.fingerprint()
                .apply { logger.d("service.fingerprint(): $this") } != BuildProp.BUILD_FINGERPRINT.apply {
                logger.d("BuildProp.BUILD_FINGERPRINT: $this")
            }
        }
            .getOrElse { false }

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

    fun reboot(reason: String = "Noop") {
        invokeService(Unit) {
            reboot(reason)
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

    fun getLogDir(): String {
        return invokeService("") {
            logDir
        }
    }

    fun hasFatalError(): Boolean {
        return invokeService(false) {
            hasFatalError()
        }
    }

    fun writeLogsTo(pfd: ParcelFileDescriptor) {
        invokeService(Unit) {
            writeLogsTo(pfd)
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

    fun getRuleCount(): Int {
        return invokeService(0) {
            ruleCount
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

    fun getDirectActionCount(): Int {
        return invokeService(0) {
            directActionCount
        }
    }

    fun executeDirectionActionById(id: String) {
        invokeService(Unit) {
            executeDirectionActionById(id)
        }
    }

    fun directExecuteRuleActions(ruleId: String) {
        invokeService(Unit) {
            directExecuteRuleActions(ruleId)
        }
    }

    fun executeAction(action: ProtoAny) {
        invokeService(Unit) {
            executeAction(ByteArrayWrapper(action.toByteArray()))
        }
    }

    fun executeActionBlocking(action: ProtoAny) {
        invokeService(Unit) {
            executeActionBlocking(ByteArrayWrapper(action.toByteArray()))
        }
    }

    fun executeMVEL(expression: String): String {
        return invokeService("") {
            executeMVEL(expression)
        }
    }

    fun addPkgSet(pkgSet: PkgSet) {
        invokeService(Unit) {
            addPkgSet(ByteArrayWrapper(pkgSet.toByteArray()))
        }
    }

    fun deletePkgSet(label: String) {
        invokeService(Unit) {
            deletePkgSet(label)
        }
    }

    fun getPkgSetByLabel(label: String): PkgSet? {
        return invokeService(null) {
            getPkgSetByLabel(label)?.let {
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

    fun evaluateCondition(condition: ProtoAny): Boolean? {
        return invokeService(null) {
            evaluateCondition(ByteArrayWrapper(condition.toByteArray()))
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


    fun getActivities(
        pkg: AppPkg
    ): List<AppComponentInfo> {
        return invokeService(emptyList()) {
            getActivities(pkg.userId, pkg.pkgName).map {
                AppComponentInfo.parseFrom(it.byte)
            }
        }
    }

    fun getReceivers(
        pkg: AppPkg
    ): List<AppComponentInfo> {
        return invokeService(emptyList()) {
            getReceivers(pkg.userId, pkg.pkgName).map {
                AppComponentInfo.parseFrom(it.byte)
            }
        }
    }

    fun getServices(pkg: AppPkg): List<AppComponentInfo> {
        return invokeService(emptyList()) {
            getServices(pkg.userId, pkg.pkgName).map {
                AppComponentInfo.parseFrom(it.byte)
            }
        }
    }

    fun getProviders(pkg: AppPkg): List<AppComponentInfo> {
        return invokeService(emptyList()) {
            getProviders(pkg.userId, pkg.pkgName).map {
                AppComponentInfo.parseFrom(it.byte)
            }
        }
    }

    fun getRunningAppPkgs(pkgSetLabel: String): List<AppPkg> {
        return invokeService(emptyList()) {
            getRunningAppPkgs(pkgSetLabel).map { AppPkg.parseFrom(it.byte) }
        }
    }

    fun getRunningServices(pkg: AppPkg): List<AppComponent> {
        return invokeService(emptyList()) {
            getRunningServices(ByteArrayWrapper(pkg.toByteArray())).map { AppComponent.parseFrom(it.byte) }
        }
    }

    fun setComponentEnabledSetting(
        component: AppComponent,
        newState: Int,
        flags: Int
    ) {
        invokeService(Unit) {
            setComponentEnabledSetting(ByteArrayWrapper(component.toByteArray()), newState, flags)
        }
    }

    fun getComponentEnabledSetting(component: AppComponent): Int {
        return invokeService(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            getComponentEnabledSetting(ByteArrayWrapper(component.toByteArray()))
        }
    }

    fun getAppComponentInfo(component: AppComponent): AppComponentInfo? {
        return invokeService(null) {
            getAppComponentInfo(ByteArrayWrapper(component.toByteArray())).let {
                AppComponentInfo.parseFrom(it.byte)
            }
        }
    }

    fun addGlobalVar(gv: GlobalVar) {
        invokeService(Unit) {
            addGlobalVar(ByteArrayWrapper(gv.toByteArray()))
        }
    }

    fun deleteGlobalVar(name: String) {
        invokeService(Unit) {
            deleteGlobalVar(name)
        }
    }

    fun getGlobalVarByName(name: String): GlobalVar? {
        return invokeService(null) {
            getGlobalVarByName(name)?.let {
                GlobalVar.parseFrom(it.byte)
            }
        }
    }

    fun getAllGlobalVars(): List<GlobalVar> {
        return invokeService(emptyList()) {
            allGlobalVars.mapNotNull {
                GlobalVar.parseFrom(it.byte)
            }
        }
    }

    fun registerGlobalVarObs(obs: IGlobalVarObserver) {
        invokeService(Unit) {
            registerGlobalVarObs(obs)
        }
    }

    fun unregisterGlobalVarObs(obs: IGlobalVarObserver) {
        invokeService(Unit) {
            unregisterGlobalVarObs(obs)
        }
    }

    fun vb(code: String, deviceId: String, callback: (CommonApiRes) -> Unit) {
        invokeService(Unit) {
            vb(code, deviceId, object : ICallback.Stub() {
                override fun receive(data: ByteArrayWrapper) {
                    callback(CommonApiRes.parseFrom(data.byte))
                }
            })
        }
    }

    fun getAllUsers(): List<UserInfo> {
        return invokeService(emptyList()) {
            allUsers.mapNotNull { UserInfo.parseFrom(it.byte) }
        }
    }

    fun setPointerLocationEnabled(enable: Boolean) {
        invokeService(Unit) {
            isPointerLocationEnabled = enable
        }
    }

    fun isPointerLocationEnabled(): Boolean {
        return invokeService(false) {
            isPointerLocationEnabled
        }
    }

    fun getActionEvaluateRecords(): List<ActionEvaluateRecord> {
        return invokeService(emptyList()) {
            actionEvaluateRecords.map {
                ActionEvaluateRecord.parseFrom(it.byte)
            }
        }
    }

    fun clearActionEvaluateRecords() {
        invokeService(Unit) {
            clearActionEvaluateRecords()
        }
    }

    fun setActionEvaluateRecorderEnabled(enable: Boolean) {
        invokeService(Unit) {
            isActionEvaluateRecorderEnabled = enable
        }
    }

    fun isActionEvaluateRecorderEnabled(): Boolean {
        return invokeService(false) {
            isActionEvaluateRecorderEnabled
        }
    }

    fun getConditionEvaluateRecords(): List<ConditionEvaluateRecord> {
        return invokeService(emptyList()) {
            conditionEvaluateRecords.map {
                ConditionEvaluateRecord.parseFrom(it.byte)
            }
        }
    }

    fun clearConditionEvaluateRecords() {
        invokeService(Unit) {
            clearConditionEvaluateRecords()
        }
    }

    fun setConditionEvaluateRecorderEnabled(enable: Boolean) {
        invokeService(Unit) {
            isConditionEvaluateRecorderEnabled = enable
        }
    }

    fun isConditionEvaluateRecorderEnabled(): Boolean {
        return invokeService(false) {
            isConditionEvaluateRecorderEnabled
        }
    }


    fun getFactPublishRecords(): List<FactPublishRecord> {
        return invokeService(emptyList()) {
            factPublishRecords.map { FactPublishRecord.parseFrom(it.byte) }
        }
    }

    fun clearFactPublishRecords() {
        invokeService(Unit) {
            clearFactPublishRecords()
        }
    }

    fun setFactPublishRecordEnabled(enable: Boolean) {
        invokeService(Unit) {
            isFactPublishRecordEnabled = enable
        }
    }

    fun isFactPublishRecordEnabled(): Boolean {
        return invokeService(false) {
            isFactPublishRecordEnabled
        }
    }

    fun getGestureAreaSize(): Int = invokeService(0) {
        gestureAreaSize
    }

    fun setGestureAreaSize(dp: Int) {
        invokeService(Unit) {
            gestureAreaSize = dp
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
    private val logger = Logger("ManagerNative")

    // IAppWidgetService
    private val androidService
        get() = IAppWidgetService.Stub.asInterface(
            ServiceManager.getService(
                Context.APPWIDGET_SERVICE
            )
        )

    // For debug purpose.
    var shortXInjection: IShortX? = null

    val shortX: IShortX? get() = shortXInjection ?: getServiceOrNull2()

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
            logger.e(it, "getServiceOrNull2")
            null
        }
    }
}