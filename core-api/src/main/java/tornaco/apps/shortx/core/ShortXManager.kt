package tornaco.apps.shortx.core

import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.IClipboard
import android.content.pm.PackageManager
import android.content.pm.ParceledListSlice
import android.content.pm.ShortcutInfo
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.ServiceManager
import android.telephony.SubscriptionInfo
import android.util.Log
import android.view.MotionEvent
import com.android.internal.appwidget.IAppWidgetService
import tornaco.apps.shortx.core.os.CancellationSignal
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AudioRecordFile
import tornaco.apps.shortx.core.proto.common.BTBondedDevice
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeLibraryQuery
import tornaco.apps.shortx.core.proto.common.CommonApiRes
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.proto.common.CustomContextMenu
import tornaco.apps.shortx.core.proto.common.CustomContextMenuSetting
import tornaco.apps.shortx.core.proto.common.DynamicShortcut
import tornaco.apps.shortx.core.proto.common.DynamicShortcutSetting
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.OCREngine
import tornaco.apps.shortx.core.proto.common.QSTileList
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.common.RemoteRuleContextAndVars
import tornaco.apps.shortx.core.proto.common.Ringtone
import tornaco.apps.shortx.core.proto.common.SensorUsage
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.proto.common.StatusBarClipUiSettings
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.common.SystemEnv
import tornaco.apps.shortx.core.proto.common.SystemSettingsAccessRecord
import tornaco.apps.shortx.core.proto.common.UserInfo
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.core.proto.common.WifiConfig
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.MethodHook
import tornaco.apps.shortx.core.proto.func.`Function` as ShortXFunction
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.plugins.ShortXPlugin
import tornaco.apps.shortx.core.proto.plugins.ShortXPluginAction
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.IForceDisableListener
import tornaco.apps.shortx.core.IRuleFeatureStateListener
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.IPkgSetObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.IToggleObserver
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.wrap
import tornaco.apps.shortx.core.code.toByteArrayWrapper
import tornaco.apps.shortx.core.code.toCodeLibraryItem
import tornaco.apps.shortx.core.code.toCodeLibraryQuery
import tornaco.apps.shortx.core.settings.defaultCustomContextMenuSetting
import tornaco.apps.shortx.core.settings.defaultDanmuUISettings
import tornaco.apps.shortx.core.settings.defaultDynamicShortcutSettings
import tornaco.apps.shortx.core.settings.defaultStatusBarTileSetting
import java.io.ByteArrayOutputStream
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicLong
import java.util.zip.Deflater
import java.util.zip.Inflater

const val CLIPBOARD_SERVICE_BINDER_CALL_PKG_NAME = "pkg.shortx.binder"

private val defaultServiceStub = IShortX.Default()

object RuleEngineNames {
    const val ENGINE_NAME_SHELL = "shell"
    const val ENGINE_NAME_MVEL = "mvel"
    const val ENGINE_NAME_SMS = "sms"
}

internal interface ManagerLogger {
    fun w(msg: Any?)
    fun i(msg: Any?)
    fun d(msg: Any?)
    fun e(err: Throwable, msg: Any?)
}

private val managerLogger = object : ManagerLogger {
    override fun w(msg: Any?) {
        Log.w("ShortXManager", msg.toString())
    }

    override fun i(msg: Any?) {
        Log.i("ShortXManager", msg.toString())
    }

    override fun d(msg: Any?) {
        Log.d("ShortXManager", msg.toString())
    }

    override fun e(err: Throwable, msg: Any?) {
        Log.e("ShortXManager", msg.toString() + "\n" + err.stackTraceToString())
    }
}

private val stubShortX by lazy {
    Proxy.newProxyInstance(
        IShortX::class.java.classLoader,
        arrayOf(IShortX::class.java),
        fun(_: Any, method: Method, args: Array<out Any>?): Any? {
            managerLogger.w("Invoke defaultServiceStub: ${method.name}")
            return method.invoke(defaultServiceStub, *(args ?: emptyArray()))
        }
    ) as IShortX
}

val stubShortXManager: ShortXManager by lazy { ShortXManager(stubShortX) }

val shortXManager: ShortXManager
    get() {
        return ShortXManager(
            (ShortXManagerNative.shortX ?: stubShortX)
        ).also {
            managerLogger.i("Accessing ShortXManager. ${it.service}")
        }
    }

class ShortXManager(val service: IShortX) {
    val isInstalled get() = kotlin.runCatching { service.fingerprint() != null }.getOrElse { false }

    companion object {
        private val _queryId = AtomicLong(System.currentTimeMillis())
        val queryId get() = _queryId.getAndIncrement()

        // 缩小，尽量避免指令丢失的问题
        const val QUERY_PAGE_SIZE = 2

        object HookFlags {
        }

        object Sensors {
            const val SENSOR_TYPE_SOUND = 100000
        }
    }

    fun isFrameworkUpdated(appFP: String): Boolean {
        return kotlin.runCatching {
            isInstalled && service.fingerprint()
                .apply { managerLogger.d("service.fingerprint(): $this") } != appFP.apply {
                managerLogger.d("BuildProp.BUILD_FINGERPRINT: $this")
            }
        }
            .getOrElse { false }
    }

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

    fun setHttpRequestLogEnabled(enabled: Boolean) {
        invokeService(Unit) {
            isHttpRequestLogEnabled = enabled
        }
    }

    fun isHttpRequestLogEnabled(): Boolean {
        return invokeService(false) {
            isHttpRequestLogEnabled
        }
    }

    fun getHttpRequestLogFD(): ParcelFileDescriptor? {
        return invokeService(null) {
            httpRequestLogFD
        }
    }

    fun getHttpRequestLogPath(): String {
        return invokeService("ERROR") {
            httpRequestLogPath
        }
    }

    fun clearHttpRequestLogs() {
        invokeService(Unit) {
            clearHttpRequestLogs()
        }
    }

    fun getJSLogFD(): ParcelFileDescriptor? {
        return invokeService(null) {
            jsLogFD
        }
    }

    fun getJSLogPath(): String {
        return invokeService("ERROR") {
            jsLogPath
        }
    }

    fun clearJSLogs() {
        invokeService(Unit) {
            clearJSLogs()
        }
    }

    fun log(message: String?, redirectTo: String?) {
        invokeService(Unit) {
            log(message, redirectTo)
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
            getRuleById(id)?.byteData?.let {
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
            val queryId = queryId.toString()
            managerLogger.d("getAllRules, queryId: $queryId")
            val count = ruleCount
            val pageSize = QUERY_PAGE_SIZE
            val pageCount: Int = count / pageSize + 1
            val list = mutableListOf<Rule>().apply {
                repeat(pageCount) { pageIndex ->
                    addAll(getAllRules(queryId, pageIndex, pageSize).map {
                        Rule.parseFrom(it.byteData)
                    })
                }
            }
            managerLogger.d("getAllRules-$queryId, list size: ${list.size}")
            list
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
                App.parseFrom(it.byteData)
            }
        }
    }

    fun getInstalledShortcutApps(): List<App> {
        return invokeService(emptyList()) {
            installedShortcutApps.map {
                App.parseFrom(it.byteData)
            }
        }
    }

    fun getAppLabel(pkg: AppPkg): String? {
        return invokeService("") {
            getAppLabel(ByteArrayWrapper(pkg.toByteArray()))
        }
    }

    fun pkgToApp(pkg: AppPkg): App? {
        return invokeService(null) {
            pkgToApp(ByteArrayWrapper(pkg.toByteArray()))?.let {
                App.parseFrom(it.byteData)
            }
        }
    }

    fun showEdgeHint(edge: List<Edge>) {
        invokeService(Unit) {
            showEdgeHint(edge.map { it.number }.toIntArray())
        }
    }

    fun showScreenRectHint(rect: Rect) {
        invokeService(Unit) {
            showScreenRectHint(ByteArrayWrapper(rect.toByteArray()))
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
            getDirectActionById(id)?.byteData?.let {
                DirectAction.parseFrom(it)
            }
        }
    }

    fun getAllDirectAction(): List<DirectAction> {
        return invokeService(emptyList()) {
            val queryId = queryId.toString()
            managerLogger.d("getAllDirectAction, queryId: $queryId")
            val daCount = directActionCount
            val pageSize = QUERY_PAGE_SIZE
            val pageCount: Int = daCount / pageSize + 1
            val list = mutableListOf<DirectAction>().apply {
                repeat(pageCount) { pageIndex ->
                    runCatching {
                        addAll(getAllDirectAction(queryId, pageIndex, pageSize).mapNotNull {
                            runCatching {
                                DirectAction.parseFrom(it.byteData)
                            }.getOrElse {
                                managerLogger.e(it, "Error parse DA.")
                                null
                            }
                        })
                    }.onFailure {
                        managerLogger.e(
                            it,
                            "Error getAllDirectAction queryId: $queryId pageIndex: $pageIndex"
                        )
                    }
                }
            }
            managerLogger.d("getAllDirectAction-$queryId, list size: ${list.size}")
            list
        }
    }

    fun getAllDirectActionPaged(pageIndex: Int, pageSize: Int): List<DirectAction> {
        return invokeService(emptyList()) {
            val list = getAllDirectAction(null, pageIndex, pageSize).mapNotNull {
                runCatching {
                    DirectAction.parseFrom(it.byteData)
                }.getOrElse {
                    managerLogger.e(it, "Error parse DA.")
                    null
                }
            }
            managerLogger.d("getAllDirectAction-$queryId, list size: ${list.size}")
            list
        }
    }

    fun getDirectActionCount(): Int {
        return invokeService(0) {
            directActionCount
        }
    }

    fun addFunction(function: ShortXFunction) {
        invokeService(Unit) {
            addFunction(ByteArrayWrapper(function.toByteArray()))
        }
    }

    fun deleteFunction(id: String) {
        invokeService(Unit) {
            deleteFunction(id)
        }
    }

    fun getFunctionById(id: String): ShortXFunction? {
        return invokeService(null) {
            getFunctionById(id)?.byteData?.let {
                ShortXFunction.parseFrom(it)
            }
        }
    }

    fun getAllFunctions(): List<ShortXFunction> {
        return invokeService(emptyList()) {
            getAllFunctions().mapNotNull {
                runCatching {
                    ShortXFunction.parseFrom(it.byteData)
                }.getOrElse { err ->
                    managerLogger.e(err, "Error parse Function.")
                    null
                }
            }
        }
    }

    fun executeDirectionActionById(context: EvaluateContext, id: String) {
        requireNotNull(context.srcId) {
            "context.srcId is null"
        }
        invokeService(Unit) {
            executeDirectionActionById(ByteArrayWrapper(context.toByteArray()), id)
        }
    }

    fun executeDirectionAction(
        context: EvaluateContext,
        da: DirectAction,
        contextAndVars: RemoteRuleContextAndVars = RemoteRuleContextAndVars.getDefaultInstance()
    ) {
        requireNotNull(context.srcId) {
            "context.srcId is null"
        }
        invokeService(Unit) {
            executeDirectionAction(
                ByteArrayWrapper(context.toByteArray()),
                ByteArrayWrapper(da.toByteArray()),
                ByteArrayWrapper(contextAndVars.toByteArray())
            )
        }
    }

    fun directExecuteRuleActions(context: EvaluateContext, ruleId: String) {
        requireNotNull(context.srcId) {
            "context.srcId is null"
        }
        invokeService(Unit) {
            directExecuteRuleActions(ByteArrayWrapper(context.toByteArray()), ruleId)
        }
    }

    // CodeLibrary methods
    fun addCodeLibraryItem(item: CodeLibraryItem) {
        invokeService(Unit) {
            addCodeLibraryItem(item.toByteArrayWrapper())
        }
    }

    fun updateCodeLibraryItem(item: CodeLibraryItem) {
        invokeService(Unit) {
            updateCodeLibraryItem(item.toByteArrayWrapper())
        }
    }

    fun deleteCodeLibraryItem(id: String) {
        invokeService(Unit) {
            deleteCodeLibraryItem(id)
        }
    }

    fun getCodeLibraryItemById(id: String): CodeLibraryItem? {
        return invokeService(null) {
            getCodeLibraryItemById(id)?.toCodeLibraryItem()
        }
    }

    fun getAllCodeLibraryItems(): List<CodeLibraryItem> {
        return invokeService(emptyList()) {
            getAllCodeLibraryItems().mapNotNull {
                runCatching {
                    it.toCodeLibraryItem()
                }.getOrElse { error ->
                    managerLogger.e(error, "Error parse CodeLibraryItem.")
                    null
                }
            }
        }
    }

    fun queryCodeLibraryItems(query: CodeLibraryQuery): List<CodeLibraryItem> {
        return invokeService(emptyList()) {
            queryCodeLibraryItems(query.toByteArrayWrapper()).mapNotNull {
                runCatching {
                    it.toCodeLibraryItem()
                }.getOrElse { error ->
                    managerLogger.e(error, "Error parse CodeLibraryItem.")
                    null
                }
            }
        }
    }

    fun getCodeLibraryItemCount(): Int {
        return invokeService(0) {
            codeLibraryItemCount
        }
    }

    fun executeCodeLibraryItem(id: String) {
        invokeService(Unit) {
            executeCodeLibraryItem(id)
        }
    }


    fun addToggle(toggle: Toggle) {
        invokeService(Unit) {
            addToggle(ByteArrayWrapper(toggle.toByteArray()))
        }
    }

    fun setToggleEnabled(id: String, enable: Boolean) {
        invokeService(Unit) {
            setToggleEnabled(id, enable)
        }
    }

    fun isToggleEnabled(id: String): Boolean {
        return invokeService(false) {
            isToggleEnabled(id)
        }
    }

    fun deleteToggle(id: String) {
        invokeService(Unit) {
            deleteToggle(id)
        }
    }

    fun getToggleById(id: String): Toggle? {
        return invokeService(null) {
            getToggleById(id)?.let {
                Toggle.parseFrom(it.byteData)
            }
        }
    }

    fun getAllToggles(): List<Toggle> {
        return invokeService(emptyList()) {
            allToggles.map {
                Toggle.parseFrom(it.byteData)
            }
        }
    }

    fun getToggleCount(): Int {
        return invokeService(0) { toggleCount }
    }

    fun registerToggleObs(obs: IToggleObserver) {
        invokeService(Unit) {
            registerToggleObs(obs)
        }
    }

    fun unregisterToggleObs(obs: IToggleObserver) {
        invokeService(Unit) {
            unregisterToggleObs(obs)
        }
    }

    fun registerConfigurationChangedListener(callback: ICallback) {
        invokeService(Unit) {
            registerConfigurationChangedListener(callback)
        }
    }

    fun unregisterConfigurationChangedListener(callback: ICallback) {
        invokeService(Unit) {
            unregisterConfigurationChangedListener(callback)
        }
    }

    fun registerGestureSettingsListener(listener: ICallback) {
        invokeService(Unit) {
            registerGestureSettingsListener(listener)
        }
    }

    fun unregisterGestureSettingsListener(listener: ICallback) {
        invokeService(Unit) {
            unregisterGestureSettingsListener(listener)
        }
    }

    fun registerFrontAppChangeListener(listener: ICallback) {
        invokeService(Unit) {
            registerFrontAppChangeListener(listener)
        }
    }

    fun unRegisterFrontAppChangeListener(listener: ICallback) {
        invokeService(Unit) {
            unRegisterFrontAppChangeListener(listener)
        }
    }

    fun registerRecorderStateListener(listener: ICallback) {
        invokeService(Unit) {
            registerRecorderStateListener(listener)
        }
    }

    fun unregisterRecorderStateListener(listener: ICallback) {
        invokeService(Unit) {
            unregisterRecorderStateListener(listener)
        }
    }

    fun onMotionEvent(event: MotionEvent) {
        invokeService(Unit) {
            onMotionEvent(event)
        }
    }

    fun shouldFireEdgeGesture(edge: Int, gesture: Int): Boolean {
        return invokeService(false) {
            shouldFireEdgeGesture(edge, gesture)
        }
    }

    fun fireEdgeGesture(edgeGesture: EdgeGesture) {
        invokeService(Unit) {
            fireEdgeGesture(ByteArrayWrapper(edgeGesture.toByteArray()))
        }
    }

    fun shouldFireScreenGesture(gesture: Int, x: Int, y: Int): Boolean {
        return invokeService(false) {
            shouldFireScreenGesture(gesture, x, y)
        }
    }

    fun fireScreenGesture(gesture: Int, x: Int, y: Int) {
        invokeService(Unit) {
            fireScreenGesture(gesture, x, y)
        }
    }

    fun showScreenAreaSelector(onSelectRect: SynchronousResultReceiver) {
        invokeService(Unit) {
            showScreenAreaSelector(onSelectRect)
        }
    }

    fun getScreenSize(): Point {
        return invokeService(Point()) {
            screenSize
        }
    }

    fun executeAction(
        context: EvaluateContext,
        action: ProtoAny,
        contextAndVars: RemoteRuleContextAndVars = RemoteRuleContextAndVars.getDefaultInstance()
    ) {
        requireNotNull(context.srcId) {
            "context.srcId is null"
        }
        invokeService(Unit) {
            executeAction(
                ByteArrayWrapper(context.toByteArray()),
                ByteArrayWrapper(action.toByteArray()),
                ByteArrayWrapper(contextAndVars.toByteArray())
            )
        }
    }

    fun executeActionBlocking(context: EvaluateContext, action: ProtoAny) {
        requireNotNull(context.srcId) {
            "context.srcId is null"
        }
        invokeService(Unit) {
            executeActionBlocking(
                ByteArrayWrapper(context.toByteArray()),
                ByteArrayWrapper(action.toByteArray())
            )
        }
    }

    fun evaluateCondition(context: EvaluateContext, condition: ProtoAny): Boolean? {
        requireNotNull(context.srcId) {
            "context.srcId is null"
        }
        return invokeService(null) {
            evaluateCondition(
                ByteArrayWrapper(context.toByteArray()),
                ByteArrayWrapper(condition.toByteArray())
            )
        }
    }

    fun executeMVEL(expression: String, context: CoroutineContext): String {
        return invokeService("") {
            executeMVEL(expression, context.number)
        }
    }

    fun executeJS(expression: String, context: CoroutineContext): String {
        return invokeService("") {
            executeJS(expression, context.number)
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
                PkgSet.parseFrom(it.byteData)
            }
        }
    }

    fun getAllPkgSets(withPkgList: Boolean = true): List<PkgSet> {
        return invokeService(emptyList()) {
            getAllPkgSets(withPkgList).map {
                PkgSet.parseFrom(it.byteData)
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
            DanmuUISettings.parseFrom(danmuUISettings.byteData)
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


    fun getActivities(
        pkg: AppPkg
    ): List<AppComponentInfo> {
        return invokeService(emptyList()) {
            getActivities(pkg.userId, pkg.pkgName).map {
                AppComponentInfo.parseFrom(it.byteData)
            }
        }
    }

    fun getReceivers(
        pkg: AppPkg
    ): List<AppComponentInfo> {
        return invokeService(emptyList()) {
            getReceivers(pkg.userId, pkg.pkgName).map {
                AppComponentInfo.parseFrom(it.byteData)
            }
        }
    }

    fun getServices(pkg: AppPkg): List<AppComponentInfo> {
        return invokeService(emptyList()) {
            getServices(pkg.userId, pkg.pkgName).map {
                AppComponentInfo.parseFrom(it.byteData)
            }
        }
    }

    fun getProviders(pkg: AppPkg): List<AppComponentInfo> {
        return invokeService(emptyList()) {
            getProviders(pkg.userId, pkg.pkgName).map {
                AppComponentInfo.parseFrom(it.byteData)
            }
        }
    }

    fun getRunningAppPkgs(pkgSetLabel: String): List<AppPkg> {
        return invokeService(emptyList()) {
            getRunningAppPkgs(pkgSetLabel).map { AppPkg.parseFrom(it.byteData) }
        }
    }

    fun getRunningServices(pkg: AppPkg): List<AppComponent> {
        return invokeService(emptyList()) {
            getRunningServices(ByteArrayWrapper(pkg.toByteArray())).map { AppComponent.parseFrom(it.byteData) }
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
                AppComponentInfo.parseFrom(it.byteData)
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
                GlobalVar.parseFrom(it.byteData)
            }
        }
    }

    fun getAllGlobalVars(): List<GlobalVar> {
        return invokeService(emptyList()) {
            allGlobalVars.mapNotNull {
                GlobalVar.parseFrom(it.byteData)
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

    fun registerPkgSetObs(obs: IPkgSetObserver) {
        invokeService(Unit) {
            registerPkgSetObs(obs)
        }
    }

    fun unregisterPkgSetObs(obs: IPkgSetObserver) {
        invokeService(Unit) {
            unregisterPkgSetObs(obs)
        }
    }

    fun registerRuleObs(obs: IRuleObserver) {
        invokeService(Unit) {
            registerRuleObs(obs)
        }
    }

    fun unregisterRuleObs(obs: IRuleObserver) {
        invokeService(Unit) {
            unregisterRuleObs(obs)
        }
    }

    fun registerDAObs(obs: IDAObserver) {
        invokeService(Unit) {
            registerDAObs(obs)
        }
    }

    fun unregisterDAObs(obs: IDAObserver) {
        invokeService(Unit) {
            unregisterDAObs(obs)
        }
    }


    fun loadOnlineRules(category: String, filterId: String, callback: (CommonApiRes) -> Unit) {
        invokeService(Unit) {
            loadOnlineRules(category, filterId, object : ICallback.Stub() {
                override fun receive(data: ByteArrayWrapper) {
                    callback(CommonApiRes.parseFrom(data.byteData))
                }
            })
        }
    }

    fun getAllUsers(): List<UserInfo> {
        return invokeService(emptyList()) {
            allUsers.mapNotNull { UserInfo.parseFrom(it.byteData) }
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
                ActionEvaluateRecord.parseFrom(it.byteData)
            }
        }
    }

    fun getEvaluatingActions(): List<String> {
        return invokeService(emptyList()) {
            evaluatingActions
        }
    }

    fun registerActionEvaluateListener(listener: IActionEvaluateListener) {
        invokeService(Unit) {
            registerActionEvaluateListener(listener)
        }
    }

    fun unregisterActionEvaluateListener(listener: IActionEvaluateListener) {
        invokeService(Unit) {
            unregisterActionEvaluateListener(listener)
        }
    }

    fun getConditionEvaluateRecords(): List<ConditionEvaluateRecord> {
        return invokeService(emptyList()) {
            conditionEvaluateRecords.map {
                ConditionEvaluateRecord.parseFrom(it.byteData)
            }
        }
    }


    fun getFactPublishRecords(): List<FactPublishRecord> {
        return invokeService(emptyList()) {
            factPublishRecords.map { FactPublishRecord.parseFrom(it.byteData) }
        }
    }

    fun clearEvaluateRecords() {
        invokeService(Unit) {
            clearEvaluateRecords()
        }
    }

    fun isEvaluateRecordEnabled(id: String): Boolean {
        return invokeService(false) {
            isEvaluateRecordEnabled(id)
        }
    }

    fun setEvaluateRecordEnabled(id: String, enable: Boolean) {
        invokeService(Unit) {
            setEvaluateRecordEnabled(id, enable)
        }
    }

    fun getGestureAreaSize(): Int = invokeService(0) {
        gestureAreaSize
    }

    fun setKeyDetectorLongPressTimeoutMS(timeout: Long) {
        invokeService(Unit) {
            keyDetectorLongPressTimeoutMS = timeout
        }
    }

    fun getKeyDetectorLongPressTimeoutMS(): Long = invokeService(0) {
        keyDetectorLongPressTimeoutMS
    }

    fun setGestureAreaSize(dp: Int) {
        invokeService(Unit) {
            gestureAreaSize = dp
        }
    }

    fun getSwipeDistanceThresholdDp(): Int {
        return invokeService(0) {
            swipeDistanceThresholdDp
        }
    }

    fun setSwipeDistanceThresholdDp(dp: Int) {
        invokeService(Unit) {
            swipeDistanceThresholdDp = dp
        }
    }

    fun getSwipeLengthScale(): Float {
        return invokeService(0f) {
            swipeLengthScale
        }
    }

    fun setSwipeLengthScale(scale: Float) {
        invokeService(Unit) {
            swipeLengthScale = scale
        }
    }

    fun hasHookFlags(flag: String): Boolean {
        return invokeService(false) { hasHookFlags(flag) }
    }

    fun addHookFlags(flag: String) {
        invokeService(Unit) {
            addHookFlags(flag)
        }
    }

    fun removeHookFlags(flag: String) {
        invokeService(Unit) {
            removeHookFlags(flag)
        }
    }

    fun setKeyEventPromptEnabled(enable: Boolean) {
        invokeService(Unit) {
            isKeyEventPromptEnabled = enable
        }
    }

    fun isKeyEventPromptEnabled(): Boolean {
        return invokeService(false) {
            isKeyEventPromptEnabled
        }
    }

    fun executeShellCommand(command: String, callback: (ShellRes) -> Unit): CancellationSignal {
        return invokeService(CancellationSignal()) {
            val cb = object : ICallback.Stub() {
                override fun receive(data: ByteArrayWrapper) {
                    val shellRes = ShellRes.parseFrom(data.byteData)
                    callback(shellRes)
                }
            }
            val signal = executeShellCommand(command, cb)
            CancellationSignal().apply {
                setRemote(signal)
            }
        }
    }

    fun isQSTileOptEnabled(): Boolean {
        return invokeService(false) { isQSTileOptEnabled }
    }

    fun setQSTileOptEnabled(enable: Boolean) {
        invokeService(Unit) {
            isQSTileOptEnabled = enable
        }
    }

    fun isInjectedShellEnabled(): Boolean {
        return invokeService(false) { isInjectedShellEnabled }
    }

    fun setInjectedShellEnabled(enable: Boolean) {
        invokeService(Unit) {
            isInjectedShellEnabled = enable
        }
    }

    fun setCustomSU(path: String?) {
        invokeService(Unit) {
            customSU = path
        }
    }

    fun getCustomSU(): String? {
        return invokeService(null) {
            customSU
        }
    }

    fun getWifiScanResults(): List<String> = invokeService(emptyList()) {
        wifiScanResults
    }

    fun getMaxSignalLevel(): Int = invokeService(5) {
        maxSignalLevel
    }

    fun getPrivilegedConfiguredNetworks(): List<String> = invokeService(emptyList()) {
        privilegedConfiguredNetworks.mapNotNull { WifiConfig.parseFrom(it.byteData).ssid }
    }

    fun getBTBondedDevices(): List<BTBondedDevice> = invokeService(emptyList()) {
        btBondedDevices.map { BTBondedDevice.parseFrom(it.byteData) }
    }

    fun isAudioRecording(): Boolean {
        return invokeService(false) { isAudioRecording }
    }

    fun stopAudioRecording() {
        invokeService(Unit) { stopAudioRecording() }
    }

    fun registerAudioRecordingObs(obs: IAudioRecordingListener) {
        invokeService(Unit) {
            registerAudioRecordingObs(obs)
        }
    }

    fun unregisterAudioRecordingObs(obs: IAudioRecordingListener) {
        invokeService(Unit) {
            unregisterAudioRecordingObs(obs)
        }
    }

    fun getAllAudioRecordFiles(): List<AudioRecordFile> {
        return invokeService(emptyList()) {
            allAudioRecordFiles.map { AudioRecordFile.parseFrom(it.byteData) }
        }
    }

    fun deleteAudioRecordFile(file: AudioRecordFile) {
        invokeService(Unit) {
            deleteAudioRecordFile(ByteArrayWrapper(file.toByteArray()))
        }
    }

    fun renameAudioRecordFile(file: AudioRecordFile, name: String) {
        invokeService(Unit) {
            renameAudioRecordFile(ByteArrayWrapper(file.toByteArray()), name)
        }
    }

    fun exportAudioRecordFile(file: AudioRecordFile, pfd: ParcelFileDescriptor) {
        invokeService(Unit) {
            exportAudioRecordFile(ByteArrayWrapper(file.toByteArray()), pfd)
        }
    }

    fun executeHttpRequest(request: HttpRequest): List<String> {
        return invokeService(emptyList()) {
            executeHttpRequest(ByteArrayWrapper(request.toByteArray()))
        }
    }

    fun getActiveSubscriptionInfoList(): List<SubscriptionInfo> {
        return invokeService(emptyList()) {
            activeSubscriptionInfoList
        }
    }

    fun getSlotLabel(slotId: Int): String? {
        return getActiveSubscriptionInfoList().firstOrNull { it.simSlotIndex == slotId }?.displayName?.toString()
    }

    fun addGestureRecord(record: GestureRecord) {
        invokeService(Unit) {
            addGestureRecord(ByteArrayWrapper(record.toByteArray()))
        }
    }

    fun getGestureRecordById(id: String): GestureRecord? {
        return invokeService(null) {
            getGestureRecordById(id)?.let {
                GestureRecord.parseFrom(it.byteData)
            }
        }
    }

    fun deleteGestureRecordById(id: String) {
        return invokeService(Unit) {
            deleteGestureRecordById(id)
        }
    }

    fun getAllGestureRecords(withEventData: Boolean): List<GestureRecord> {
        return invokeService(emptyList()) {
            getAllGestureRecords(withEventData).map {
                GestureRecord.parseFrom(it.byteData)
            }
        }
    }

    fun injectGestureRecord(
        id: String,
        speed: Float = 1f,
        onComplete: SynchronousResultReceiver? = null
    ) {
        invokeService(Unit) {
            injectGestureRecord(id, speed, onComplete)
        }
    }

    fun startGestureRecording() {
        invokeService(Unit) {
            startGestureRecording()
        }
    }

    fun stopGestureRecording(save: Boolean) {
        invokeService(Unit) {
            stopGestureRecording(save)
        }
    }

    fun isGestureRecording(): Boolean {
        return invokeService(false) { isGestureRecording }
    }

    fun getAllStatusBarTileSettings(): List<StatusBarTileSetting> {
        return invokeService(emptyList()) {
            allStatusBarTileSettings.map {
                StatusBarTileSetting.parseFrom(it.byteData)
            }
        }
    }

    fun getStatusBarTileSettingsByNumber(tileNumber: Int): StatusBarTileSetting {
        return invokeService(null) {
            getStatusBarTileSettingsByNumber(tileNumber)?.let {
                StatusBarTileSetting.parseFrom(it.byteData)
            }
        } ?: defaultStatusBarTileSetting(StatusBarTile.forNumber(tileNumber))
    }

    fun updateStatusBarTileSetting(tileSetting: StatusBarTileSetting) {
        invokeService(Unit) {
            updateStatusBarTileSetting(ByteArrayWrapper(tileSetting.toByteArray()))
        }
    }

    fun updateQSTileActiveState(number: Int) {
        invokeService(Unit) {
            updateQSTileActiveState(number)
        }
    }

    fun registerStatusBarTileSettingsChangeListener(listener: IStatusBarTileSettingsChangeListener) {
        invokeService(Unit) {
            registerStatusBarTileSettingsChangeListener(listener)
        }
    }

    fun unregisterStatusBarTileSettingsChangeListener(listener: IStatusBarTileSettingsChangeListener) {
        invokeService(Unit) {
            unregisterStatusBarTileSettingsChangeListener(listener)
        }
    }

    fun getAllDynamicShortcutSettings(): List<DynamicShortcutSetting> {
        return invokeService(emptyList()) {
            getAllDynamicShortcutSettings().mapNotNull {
                DynamicShortcutSetting.parseFrom(it.byteData)
            }
        }
    }

    fun getDynamicShortcutSettingsByNumber(number: Int): DynamicShortcutSetting? {
        return invokeService(null) { getDynamicShortcutSettingsByNumber(number) }?.let {
            DynamicShortcutSetting.parseFrom(it.byteData)
        } ?: defaultDynamicShortcutSettings(DynamicShortcut.forNumber(number))
    }

    fun updateDynamicShortcutSetting(setting: DynamicShortcutSetting) {
        invokeService(Unit) {
            updateDynamicShortcutSetting(ByteArrayWrapper(setting.toByteArray()))
        }
    }

    fun isSettingsCatcherEnabled(): Boolean {
        return invokeService(false) {
            isSettingsCatcherEnabled
        }
    }

    fun setSettingsCatcherEnabled(enabled: Boolean) {
        invokeService(Unit) {
            isSettingsCatcherEnabled = enabled
        }
    }

    fun onReadSettings(url: String, value: String) {
        invokeService(Unit) {
            onReadSettings(url, value)
        }
    }

    fun onWriteSettings(url: String, value: String) {
        invokeService(Unit) {
            onWriteSettings(url, value)
        }
    }

    fun getReadSettingsRecord(): List<SystemSettingsAccessRecord> {
        return invokeService(emptyList()) {
            readSettingsRecord.map {
                SystemSettingsAccessRecord.parseFrom(it.byteData)
            }
        }
    }

    fun getWriteSettingsRecord(): List<SystemSettingsAccessRecord> {
        return invokeService(emptyList()) {
            writeSettingsRecord.map {
                SystemSettingsAccessRecord.parseFrom(it.byteData)
            }
        }
    }

    fun clearWriteSettingsRecord() {
        invokeService(Unit) {
            clearWriteSettingsRecord()
        }
    }

    fun clearReadSettingsRecord() {
        invokeService(Unit) {
            clearReadSettingsRecord()
        }
    }

    fun setRuleEngineEnabled(engine: String, enable: Boolean) {
        invokeService(Unit) {
            setRuleEngineEnabled(engine, enable)
        }
    }

    fun isRuleEngineEnabled(engine: String): Boolean {
        return invokeService(false) {
            isRuleEngineEnabled(engine)
        }
    }

    fun setForceDisableShortXByKeyPressEnabled(enable: Boolean) {
        invokeService(Unit) {
            isForceDisableShortXByKeyPressEnabled = enable
        }
    }

    fun isForceDisableShortXByKeyPressEnabled(): Boolean {
        return invokeService(false) {
            isForceDisableShortXByKeyPressEnabled
        }
    }

    fun registerForceDisableListener(listener: IForceDisableListener) {
        invokeService(Unit) {
            registerForceDisableListener(listener)
        }
    }

    fun unregisterForceDisableListener(listener: IForceDisableListener) {
        invokeService(Unit) {
            unregisterForceDisableListener(listener)
        }
    }

    fun registerRuleFeatureStateListener(listener: IRuleFeatureStateListener) {
        invokeService(Unit) {
            registerRuleFeatureStateListener(listener)
        }
    }

    fun unregisterRuleFeatureStateListener(listener: IRuleFeatureStateListener) {
        invokeService(Unit) {
            unregisterRuleFeatureStateListener(listener)
        }
    }

    fun addOrUpdateRuleSet(rs: RuleSet) {
        invokeService(Unit) {
            addOrUpdateRuleSet(ByteArrayWrapper(rs.toByteArray()))
        }
    }

    fun deleteRuleSet(id: String) {
        invokeService(Unit) {
            deleteRuleSet(id)
        }
    }

    fun getRuleSetById(id: String, withRules: Boolean = false): RuleSet? {
        return invokeService(null) {
            getRuleSetById(id, withRules)?.let { RuleSet.parseFrom(it.byteData) }
        }
    }

    fun getAllRuleSets(withRules: Boolean = false): List<RuleSet> {
        return invokeService(emptyList()) {
            getAllRuleSets(withRules).map { RuleSet.parseFrom(it.byteData) }
        }
    }

    fun addOrUpdateDASet(ds: DirectActionSet) {
        invokeService(Unit) {
            addOrUpdateDASet(ByteArrayWrapper(ds.toByteArray()))
        }
    }

    fun deleteDASet(id: String) {
        invokeService(Unit) {
            deleteDASet(id)
        }
    }

    fun getDASetById(id: String, withDAs: Boolean = false): DirectActionSet? {
        return invokeService(null) {
            getDASetById(id, withDAs)?.let { DirectActionSet.parseFrom(it.byteData) }
        }
    }

    fun getAllDASets(withDAs: Boolean = false): List<DirectActionSet> {
        return invokeService(emptyList()) {
            getAllDASets(withDAs).map { DirectActionSet.parseFrom(it.byteData) }
        }
    }

    fun getAllQSTiles(): QSTileList {
        return invokeService(QSTileList.getDefaultInstance()) {
            allQSTiles.let {
                QSTileList.parseFrom(it.byteData)
            }
        }
    }

    fun getStreamMinVolume(type: Int): Int {
        return invokeService(0) {
            getStreamMinVolume(type)
        }
    }

    fun getStreamMaxVolume(type: Int): Int {
        return invokeService(0) {
            getStreamMaxVolume(type)
        }
    }

    fun getStreamVolume(type: Int): Int {
        return invokeService(0) {
            getStreamVolume(type)
        }
    }

    fun setStartActivityResult(requestCode: Int, resultCode: Int, result: Bundle) {
        invokeService(Unit) {
            setStartActivityResult(requestCode, resultCode, result)
        }
    }

    fun setRuleFeatureEnabled(enable: Boolean) {
        invokeService(Unit) {
            isRuleFeatureEnabled = enable
        }
    }

    fun isRuleFeatureEnabled(): Boolean {
        return invokeService(false) {
            isRuleFeatureEnabled
        }
    }

    fun querySensorUsage(): List<SensorUsage> {
        return invokeService(emptyList()) {
            querySensorUsages().map {
                SensorUsage.parseFrom(it.byteData)
            }
        }
    }

    fun registerSensorUsageListener(listener: ISensorUsageListener) {
        invokeService(Unit) {
            registerSensorUsageListener(listener)
        }
    }

    fun unregisterSensorUsageListener(listener: ISensorUsageListener) {
        invokeService(Unit) {
            unregisterSensorUsageListener(listener)
        }
    }

    fun setPostNByAppEnabled(enable: Boolean) {
        invokeService(Unit) {
            isPostNByAppEnabled = enable
        }
    }

    fun isPostNByAppEnabled(): Boolean {
        return invokeService(false) {
            isPostNByAppEnabled
        }
    }

    @Deprecated("Moved to ShortX.Ext")
    fun installApk(
        packageName: String,
        fd: ParcelFileDescriptor,
        onSuccess: ICallback,
        onFailure: ICallback
    ) {
        invokeService(Unit) {
            installApk(packageName, fd, onSuccess, onFailure)
        }
    }

    fun deepLinkTriggerCalled(tag: String) {
        invokeService(Unit) {
            deepLinkTriggerCalled(tag)
        }
    }

    fun getAllRingtones(): List<Ringtone> {
        return invokeService(emptyList()) {
            allRingtones.map {
                Ringtone.parseFrom(it.byteData)
            }
        }
    }

    fun registerNFCTagEndpointListener(listener: INFCTagEndpointListener) {
        invokeService(Unit) {
            registerNFCTagEndpointListener(listener)
        }
    }

    fun unregisterNFCTagEndpointListener(listener: INFCTagEndpointListener) {
        invokeService(Unit) {
            unregisterNFCTagEndpointListener(listener)
        }
    }

    fun onHookedMethodCalled(methodHook: MethodHook, methodArgs: List<StringPair>) {
        invokeService(Unit) {
            onHookedMethodCalled(ByteArrayWrapper(methodHook.toByteArray()), methodArgs.map {
                ByteArrayWrapper(it.toByteArray())
            })
        }
    }

    fun getMethodHookLogFD(): ParcelFileDescriptor? = invokeService(null) {
        methodHookLogFD
    }

    fun getMethodHookLogPath(): String = invokeService("ERROR") {
        methodHookLogPath
    }

    fun clearMethodHookLogs() = invokeService(Unit) {
        clearMethodHookLogs()
    }

    fun getRequestPinShortcuts(): List<ShortcutInfo> {
        return invokeService(emptyList()) {
            requestPinShortcuts
        }
    }

    fun getRequestPinAppWidgets(): List<AppWidgetProviderInfo> {
        return invokeService(emptyList()) {
            requestPinAppWidgets
        }
    }

    fun getInstalledPlugins(): List<ShortXPlugin> {
        return invokeService(emptyList()) {
            installedPlugins.mapNotNull {
                ShortXPlugin.parseFrom(it.byteData)
            }
        }
    }

    fun getInstalledPluginActions(): List<ShortXPluginAction> {
        return invokeService(emptyList()) {
            installedPluginActions.mapNotNull {
                ShortXPluginAction.parseFrom(it.byteData)
            }
        }
    }

    fun getInstalledPluginConditions(): List<ByteArrayWrapper> {
        TODO("Not yet implemented")
    }

    fun getInstalledPluginFacts(): List<ByteArrayWrapper> {
        TODO("Not yet implemented")
    }

    fun getStatusbarClipSettings(): StatusBarClipUiSettings {
        return invokeService(StatusBarClipUiSettings.getDefaultInstance()) {
            statusbarClipSettings.byteData.let {
                StatusBarClipUiSettings.parseFrom(it)
            }
        }
    }

    fun setStatusbarClipSettings(settings: StatusBarClipUiSettings) {
        invokeService(Unit) {
            statusbarClipSettings = settings.toByteArray().wrap()
        }
    }

    fun addWebDavProfile(profile: WebDavProfile) {
        invokeService(Unit) {
            addWebDavProfile(ByteArrayWrapper(profile.toByteArray()))
        }
    }

    fun removeWebDavProfile(id: String) {
        invokeService(Unit) {
            removeWebDavProfile(id)
        }
    }

    fun getWebDavProfileById(id: String): WebDavProfile? {
        return invokeService(null) {
            getWebDavProfileById(id)?.let {
                WebDavProfile.parseFrom(it.byteData)
            }
        }
    }

    fun getAllWebDavProfiles(): List<WebDavProfile> {
        return invokeService(emptyList()) {
            allWebDavProfiles.mapNotNull {
                WebDavProfile.parseFrom(it.byteData)
            }
        }
    }

    fun getAllSystemEnv(): List<SystemEnv> {
        return invokeService(emptyList()) {
            allSystemEnv.mapNotNull {
                SystemEnv.parseFrom(it.byteData)
            }
        }
    }

    fun isLightBarTransitionsIconsDark(): Boolean {
        return invokeService(false) {
            isLightBarTransitionsIconsDark
        }
    }

    fun setOCREngine(engine: OCREngine) {
        invokeService(Unit) {
            ocrEngine = engine.number
        }
    }

    fun getOCREngine(): OCREngine {
        return invokeService(OCREngine.OCREngine_Paddle) {
            OCREngine.forNumber(ocrEngine)
        }
    }

    fun getAllContextMenuSettings(): List<CustomContextMenuSetting> {
        return invokeService(emptyList()) {
            allContextMenuSettings.mapNotNull {
                CustomContextMenuSetting.parseFrom(it.byteData)
            }
        }
    }

    fun getContextMenuSettingsByMenuNumber(number: Int): CustomContextMenuSetting {
        return invokeService(null) {
            CustomContextMenuSetting.parseFrom(getContextMenuSettingsByMenuNumber(number)?.byteData)
        } ?: defaultCustomContextMenuSetting(CustomContextMenu.forNumber(number))
    }

    fun updateContextMenuSetting(setting: CustomContextMenuSetting) {
        invokeService(Unit) {
            updateContextMenuSetting(ByteArrayWrapper(setting.toByteArray()))
        }
    }

    fun onMenuActionTrigger(number: Int, text: String) {
        invokeService(Unit) {
            onMenuActionTrigger(number, text)
        }
    }

    fun setAppFeatureEnabled(feat: Int, enabled: Boolean) {
        invokeService(Unit) {
            setAppFeatureEnabled(feat, enabled)
        }
    }

    fun isAppFeatureEnabled(feat: Int): Boolean = invokeService(false) {
        isAppFeatureEnabled(feat)
    }

    private inline fun <T> invokeService(defaultValue: T, onService: IShortX.() -> T): T {
        return kotlin.runCatching {
            onService(service)
        }.getOrElse {
            managerLogger.e(it, "invokeService error")
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
            managerLogger.e(it, "invokeService error, arg: ${logTokens.joinToString(", ")}")
            defaultValue
        }
    }
}

object ShortXManagerNative {
    // IAppWidgetService
    private val widgetService: IAppWidgetService?
        get() = IAppWidgetService.Stub.asInterface(
            ServiceManager.getService(
                Context.APPWIDGET_SERVICE
            )
        )

    private var remoteService: IShortX? = null
        set(value) {
            managerLogger.i("Set remoteService: $value")
            field = value
        }

    val shortX: IShortX?
        get() = ((remoteService ?: getServiceOrNull2()) ?: getServiceOrNull3())?.also {
            managerLogger.i("Caching remoteService: $it")
            remoteService = it
        }

    // To install a local service instance in System process or for debug purpose.
    fun attachLocalService(shortX: IShortX) {
        managerLogger.i("attachLocalService: $shortX")
        this.remoteService = shortX
    }

    private fun getServiceOrNull2(): IShortX? {
        return kotlin.runCatching {
            val print = widgetService ?: return null
            val list: ParceledListSlice<*> =
                print.startListening(null, "shortx", 6, intArrayOf(2, 0, 2, 3))
            managerLogger.w("getServiceOrNull2, shortx list: $list")
            val bundle = list.list[0] as Bundle
            val shortx = bundle.getBinder("shortX-Binder")
            managerLogger.w("getServiceOrNull2, shortx binder: $shortx")
            IShortX.Stub.asInterface(shortx)
        }.getOrElse {
            managerLogger.e(it, "getServiceOrNull2")
            null
        }
    }

    private fun getServiceOrNull3(): IShortX? {
        return kotlin.runCatching {
            val clipService = IClipboard.Stub.asInterface(
                ServiceManager.getServiceOrThrow(Context.CLIPBOARD_SERVICE)
            )
            val clipData =
                clipService.getPrimaryClip(CLIPBOARD_SERVICE_BINDER_CALL_PKG_NAME, "shortx", 0, 0)
            val clipCount = clipData.itemCount
            val lastItem = clipData.getItemAt(clipCount - 1)
            val intent = lastItem.intent
            val bundle = intent.getBundleExtra("shortx-bundle")
            val shortxBinder = bundle.getBinder("shortx-binder")
            managerLogger.w("getServiceOrNull3, ClipboardService shortx binder: $shortxBinder")
            val shortx = IShortX.Stub.asInterface(shortxBinder)
            managerLogger.w("getServiceOrNull3, shortx: $shortx")
            shortx
        }.getOrElse {
            managerLogger.e(it, "getServiceOrNull3")
            null
        }
    }
}

internal object ByteArrayCompressor {

    fun compress(data: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(data)
        deflater.finish()

        val buffer = ByteArray(1024)
        val outputStream = ByteArrayOutputStream()
        try {
            while (!deflater.finished()) {
                val count = deflater.deflate(buffer)
                outputStream.write(buffer, 0, count)
            }
        } finally {
            deflater.end()
        }
        return outputStream.toByteArray().also {
            Log.v("ByteArrayCompressor", "After compress size: ${it.size}")
        }
    }


    fun decompress(data: ByteArray): ByteArray {
        return runCatching {
            val inflater = Inflater()
            inflater.setInput(data)

            val buffer = ByteArray(1024)
            val outputStream = ByteArrayOutputStream()
            try {
                while (!inflater.finished()) {
                    val count = inflater.inflate(buffer)
                    outputStream.write(buffer, 0, count)
                }
            } finally {
                inflater.end()
            }
            outputStream.toByteArray()
        }.getOrElse {
            // Fallback to raw data for compat(shortcut stub) issue.
            data
        }
    }
}