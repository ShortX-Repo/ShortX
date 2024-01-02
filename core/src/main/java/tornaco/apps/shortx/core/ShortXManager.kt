package tornaco.apps.shortx.core

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ParceledListSlice
import android.graphics.Bitmap
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.ServiceManager
import android.telephony.SubscriptionInfo
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
import tornaco.apps.shortx.core.proto.common.CommonApiRes
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.QSTileList
import tornaco.apps.shortx.core.proto.common.SensorUsage
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.proto.common.SystemSettingsAccessRecord
import tornaco.apps.shortx.core.proto.common.UserInfo
import tornaco.apps.shortx.core.proto.common.WifiConfig
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.IPkgSetObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.IToggleObserver
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

object RuleEngineNames {
    const val ENGINE_NAME_SHELL = "shell"
    const val ENGINE_NAME_MVEL = "mvel"
}

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

class ShortXManager(val service: IShortX) {
    val isInstalled get() = kotlin.runCatching { service.fingerprint() != null }.getOrElse { false }

    companion object {
        object HookFlags {
            const val FLAG_DISABLE_GESTURE_HOOK = "disable_gesture_hooks"
        }
    }

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
            allRules.map {
                Rule.parseFrom(it.byteData)
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

    fun getAppLabel(pkg: AppPkg): String {
        return invokeService("Unknown") {
            getAppLabel(ByteArrayWrapper(pkg.toByteArray())) ?: "Unknown"
        }
    }

    fun pkgToApp(pkg: AppPkg): App? {
        return invokeService(null) {
            pkgToApp(ByteArrayWrapper(pkg.toByteArray()))?.let {
                App.parseFrom(it.byteData)
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
            getDirectActionById(id)?.byteData?.let {
                DirectAction.parseFrom(it)
            }
        }
    }

    fun getAllDirectAction(): List<DirectAction> {
        return invokeService(emptyList()) {
            allDirectAction.map {
                DirectAction.parseFrom(it.byteData)
            }
        }
    }

    fun getDirectActionCount(): Int {
        return invokeService(0) {
            directActionCount
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

    fun executeDirectionAction(context: EvaluateContext, da: DirectAction) {
        requireNotNull(context.srcId) {
            "context.srcId is null"
        }
        invokeService(Unit) {
            executeDirectionAction(
                ByteArrayWrapper(context.toByteArray()),
                ByteArrayWrapper(da.toByteArray())
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

    fun executeAction(context: EvaluateContext, action: ProtoAny) {
        requireNotNull(context.srcId) {
            "context.srcId is null"
        }
        invokeService(Unit) {
            executeAction(
                ByteArrayWrapper(context.toByteArray()),
                ByteArrayWrapper(action.toByteArray())
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

    fun executeMVEL(expression: String): String {
        return invokeService("") {
            executeMVEL(expression)
        }
    }

    fun executeJS(expression: String): String {
        return invokeService("") {
            executeJS(expression)
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


    fun vb(code: String, deviceId: String, callback: (CommonApiRes) -> Unit) {
        invokeService(Unit) {
            vb(code, deviceId, object : ICallback.Stub() {
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

    fun getWifiScanResults(): List<String> = invokeService(emptyList()) {
        wifiScanResults
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

    fun getStatusBarTileSettingsByNumber(tileNumber: Int): StatusBarTileSetting? {
        return invokeService(null) {
            getStatusBarTileSettingsByNumber(tileNumber)?.let {
                StatusBarTileSetting.parseFrom(it.byteData)
            }
        }
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