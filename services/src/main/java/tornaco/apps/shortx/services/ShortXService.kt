package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.ServiceManager
import android.os.ShellCommand
import android.os.UserHandle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.*
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AudioRecordFile
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.proto.common.UserInfo
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.fact.OnQSTileClick
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.IPkgSetObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.IToggleObserver
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.emptyByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.systemui.IQSTileHost
import tornaco.apps.shortx.core.util.ApkUtils
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.UserHandleUtils.userIdToUserHandle
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.core.util.statusBarManager
import tornaco.apps.shortx.core.util.wifiManager
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequest.handleResponseBody
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequest.toHttpRequestBody
import tornaco.apps.shortx.services.rule.fact.trigger.BTHelper
import tornaco.apps.shortx.services.rule.nextEvaluateContextSerialNumber
import tornaco.apps.shortx.services.ui.I18NCache
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.v.v
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import java.io.File

@SuppressLint("StaticFieldLeak")
object ShortXService : SystemService(ShortXContextImpl(), "ShortXService") {
    private val serviceLifecycle by lazy { ServiceLifecycle(shortXContext) }

    fun main() {
        LogInit.initEarlyLogging()
    }

    override fun start(context: Context) {
        super.start(context)
        LogInit.initStartedLogging()
        serviceLifecycle.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        //  publishBinderService()
        // Init i18N
        val appRes = AppResources(context, BuildProp.APP_PACKAGE_NAME)
        val localeName = appRes.getString("ui_i18n_name")
        I18NCache.init(appRes.appContext, localeName)
        val testRes = i18NIns["ui.common.delete"]
        logger.d("testRes: $testRes")
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

    fun onCommand(shellCommand: ShellCommand): Int {
        enforceCallingPermissions()

        val pw = shellCommand.outPrintWriter
        val nextArgs: String? = shellCommand.nextArg
        if (nextArgs == null) {
            pw.println("Welcome to ShortX(${BuildProp.APP_VERSION_NAME}). Command usage:")
            pw.println("shortx run [ID]")
            pw.println("      Run a Rule.")
            pw.println("shortx dump")
            pw.println("      Dump ShortX states.")
            pw.flush()
            return 0
        } else if (nextArgs == "run") {
            val id: String? = shellCommand.nextArg
            if (id == null) {
                pw.println("Missing id argument")
                pw.flush()
                return 1
            }
            pw.println("Checking id: $id")
            val da = shortXContext.rule.getDirectActionById(id)
            if (da == null) {
                pw.println("Rule not exists.")
                pw.flush()
                return 1
            }
            pw.println("About to run: ${da.title}")
            pw.flush()
            shortXContext.rule.executeDirectionActionById(
                context = EvaluateContext.newBuilder()
                    .setSrcId(id)
                    .setSrcTitle("Shell command")
                    .setSrcEvent(ContextSrcEvent.ContextSrc_DirectAction)
                    .setSerialNumber(nextEvaluateContextSerialNumber())
                    .build(),
                id = id
            )
            return 0
        } else if (nextArgs == "dump") {
            pw.println("Running actions:")
            shortXContext.rule.getEvaluatingActions().forEach {
                pw.println(it)
            }
            pw.flush()
            return 0
        }

        return 1
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

        override fun getLogDir(): String {
            return Dirs.baseServerLogDir().absolutePath
        }

        override fun hasFatalError(): Boolean {
            return shortXContext.watchDog.hasFatalError()
        }

        override fun writeLogsTo(pfd: ParcelFileDescriptor) {
            LogInit.writeLogsTo(pfd)
        }

        override fun addRule(rule: ByteArrayWrapper) {
            v(context) {
                shortXContext.rule.addOrUpdateRule(Rule.parseFrom(rule.byteData))
            }
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
            return v(context) {
                shortXContext.rule.getAllRules().map {
                    ByteArrayWrapper(it.toByteArray())
                }
            } ?: emptyList()
        }

        override fun getRuleCount(): Int {
            return shortXContext.rule.getRuleCount()
        }

        override fun getInstalledApps(): List<ByteArrayWrapper> {
            return shortXContext.appState.getInstalledApps().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getAppLabel(pkg: ByteArrayWrapper): String? {
            return shortXContext.appState.getAppLabel(AppPkg.parseFrom(pkg.byteData))
        }

        override fun pkgToApp(pkg: ByteArrayWrapper): ByteArrayWrapper? {
            return shortXContext.appState.pkgToApp(AppPkg.parseFrom(pkg.byteData))?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun showEdgeHint(edgeNumber: Int) {
            shortXContext.gesture.showEdgeHint(edgeNumber)
        }

        override fun addDirectAction(da: ByteArrayWrapper) {
            enforceCallingPermissions()
            v(context) {
                shortXContext.rule.addDirectAction(DirectAction.parseFrom(da.byteData))
            }
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
            return v(context) {
                shortXContext.rule.getAllDirectAction().map {
                    ByteArrayWrapper(it.toByteArray())
                }
            } ?: emptyList()
        }

        override fun getDirectActionCount(): Int {
            return shortXContext.rule.getDirectActionCount()
        }

        override fun executeDirectionActionById(
            evaluateContext: ByteArrayWrapper,
            id: String
        ) {
            enforceCallingPermissions()
            shortXContext.rule.executeDirectionActionById(
                context = EvaluateContext.parseFrom(evaluateContext.byteData),
                id = id
            )
        }

        override fun executeDirectionAction(
            evaluateContext: ByteArrayWrapper,
            da: ByteArrayWrapper
        ) {
            enforceCallingPermissions()
            shortXContext.rule.executeDirectionAction(
                context = EvaluateContext.parseFrom(evaluateContext.byteData),
                directAction = DirectAction.parseFrom(da.byteData)
            )
        }

        override fun evaluateCondition(
            evaluateContext: ByteArrayWrapper,
            condition: ByteArrayWrapper
        ): Boolean {
            return shortXContext.rule.evaluateCondition(
                context = EvaluateContext.parseFrom(evaluateContext.byteData),
                condition = ProtoAny.parseFrom(condition.byteData)
            )
        }

        override fun onMediaStoreInsert(uri: Uri, path: String) {
            shortXContext.media.onMediaStoreInsert(uri, path)
        }

        override fun onMediaStoreDelete(uri: Uri, path: String) {
            shortXContext.media.onMediaStoreDelete(uri, path)
        }

        override fun executeShellCommand(command: String): String {
            enforceCallingPermissions()
            logger.d("executeShellCommand: $command")
            return shortXContext.rule.executeShell(command)
        }

        override fun getWifiScanResults(): List<String> {
            enforceCallingPermissions()
            return v(context) {
                runClearCallingIdentity {
                    context.wifiManager.scanResults.mapNotNull { it.SSID }
                }
            } ?: emptyList()
        }

        override fun attachSUImpl(from: String?, su: ISu?) {
            shortXContext.shell.attachSUImpl(from, su)

            systemServiceScope.launchSafety {
                // Sometimes SystemUI attached before system ready called, something has not been initilised...
                while (!serviceLifecycle.isSystemReadyCalled) {
                    delay(1000)
                    logger.w("attachSUImpl, Wait 1s until serviceLifecycle.isSystemReadyCalled.")
                }

                logger.i("About to publish SystemReady")
                delay(3000)

                shortXContext.user.publishSystemReady()
            }
        }

        override fun attachQSTileHost(host: IQSTileHost) {
            shortXContext.ui.attachQSTileHost(host)
        }

        override fun getAllQSTiles(): ByteArrayWrapper {
            return shortXContext.ui.getAllQSTiles().let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getSwipeDistanceThresholdDp(): Int {
            return shortXContext.gesture.getSwipeDistanceThresholdDp()
        }

        override fun setSwipeDistanceThresholdDp(dp: Int) {
            shortXContext.gesture.setSwipeDistanceThresholdDp(dp)
        }

        override fun log(message: String?) {
            logger.i("EXTERNAL: $message")
        }

        override fun getStreamMinVolume(type: Int): Int {
            return shortXContext.audio.getStreamMinVolume(type)
        }

        override fun getStreamMaxVolume(type: Int): Int {
            return shortXContext.audio.getStreamMaxVolume(type)
        }

        override fun getStreamVolume(type: Int): Int {
            return shortXContext.audio.getStreamVolume(type)
        }

        override fun isInjectedShellEnabled(): Boolean {
            return shortXContext.shell.isInjectedShellEnabled()
        }

        override fun setInjectedShellEnabled(enable: Boolean) {
            shortXContext.shell.setInjectedShellEnabled(enable)
        }

        override fun isAudioRecording(): Boolean {
            return shortXContext.audio.isRecording
        }

        override fun stopAudioRecording() {
            shortXContext.audio.stopRecording()
        }

        override fun registerAudioRecordingObs(obs: IAudioRecordingListener) {
            shortXContext.audio.registerAudioRecordingObs(obs)
        }

        override fun unregisterAudioRecordingObs(obs: IAudioRecordingListener) {
            shortXContext.audio.unregisterAudioRecordingObs(obs)
        }

        override fun getAllAudioRecordFiles(): List<ByteArrayWrapper> {
            return shortXContext.audio.getAllAudioRecordFiles()
                .map { ByteArrayWrapper(it.toByteArray()) }
        }

        override fun deleteAudioRecordFile(file: ByteArrayWrapper) {
            shortXContext.audio.deleteAudioRecordFile(AudioRecordFile.parseFrom(file.byteData))
        }

        override fun exportAudioRecordFile(file: ByteArrayWrapper, pfd: ParcelFileDescriptor?) {
            logger.w("exportAudioRecordFile: $file $pfd")
            shortXContext.audio.exportAudioRecordFile(
                AudioRecordFile.parseFrom(file.byteData),
                requireNotNull(pfd)
            )
        }

        override fun getBTBondedDevices(): List<ByteArrayWrapper> {
            return v(context) {
                runClearCallingIdentity {
                    BTHelper.getBTBondedDevices().map { ByteArrayWrapper(it.toByteArray()) }
                }
            } ?: emptyList()
        }

        override fun attachNotificationPoster(from: String, poster: INotificationPoster) {
            shortXContext.notification.attachNotificationPoster(from, poster)
        }

        override fun uncaughtException(error: String) {
            logger.e("PROCESS ERROR")
            val uid = Binder.getCallingUid()
            val pkgName = if (uid == 1000) {
                "android"
            } else {
                shortXContext.appState.uidToApp(uid)?.pkg?.pkgName ?: "Unknown_Pkg"
            }
            logger.e("UID: $uid")
            logger.e("pkg: $pkgName")
            logger.e("uncaughtException: $error")

            // Write to file
            val logFile = File(
                File(Dirs.baseServerLogDir(), "crash"),
                "$pkgName-$uid-${DateUtils.formatForFileName(System.currentTimeMillis())}.log"
            )
            logFile.parentFile.mkdirs()
            logFile.writeText(error)
        }

        override fun crashSystemServer() {
            enforceCallingPermissions()
            handler.post {
                logger.e("------   crashSystemServer ---------")
                throw IllegalStateException("Crash")
            }
        }

        override fun reportFATALError(error: String) {
            shortXContext.watchDog.onFatalError(Error(error), "reportFATALError")
        }

        override fun executeHttpRequest(request: ByteArrayWrapper): List<String> {
            val localVars = LocalVars.obtain()
            val data = HttpRequest.parseFrom(request.byteData)
            val url = data.url
                .replaceLocalVarValues(localVars)
                .replaceGlobalVarValues {
                    shortXContext.rule.getAllGlobalVars()
                }
            val requestBody =
                data.requestBody.toHttpRequestBody(
                    ContextData.obtain(),
                    localVars
                ) {
                    shortXContext.rule.getAllGlobalVars()
                }
            return kotlin.runCatching {
                tornaco.apps.shortx.services.rule.actor.impl.HttpRequest.callUrl(
                    url = url,
                    method = data.method.name,
                    headers = data.headersList.associate {
                        it.key to (it.value)
                            .replaceLocalVarValues(localVars)
                            .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
                    },
                    requestBody = requestBody,
                ).let {
                    handleResponseBody(
                        contextData = ContextData.obtain(),
                        globalVarsLazy = { shortXContext.rule.getAllGlobalVars() },
                        localVars = LocalVars.obtain(),
                        response = it, adapter = data.adapter
                    )
                }
            }.getOrElse {
                listOf(Log.getStackTraceString(it))
            }
        }

        @SuppressLint("MissingPermission")
        override fun getActiveSubscriptionInfoList(): List<SubscriptionInfo> {
            enforceCallingPermissions()
            val res = mutableListOf<SubscriptionInfo>()
            runBlocking {
                systemServiceScope.launchSafety {
                    SubscriptionManager.from(context).activeSubscriptionInfoList.apply {
                        res.addAll(this)
                    }
                }.join()
            }
            return res
        }

        override fun addGestureRecord(record: ByteArrayWrapper) {
            shortXContext.gesture.addGestureRecord(GestureRecord.parseFrom(record.byteData))
        }

        override fun getGestureRecordById(id: String): ByteArrayWrapper? {
            return shortXContext.gesture.getGestureRecordById(id)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun deleteGestureRecordById(id: String) {
            shortXContext.gesture.deleteGestureRecordById(id)
        }

        override fun getAllGestureRecords(withEventData: Boolean): List<ByteArrayWrapper> {
            return shortXContext.gesture.getAllGestureRecords(withEventData).map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun injectGestureRecord(
            id: String,
            speed: Float,
            onComplete: SynchronousResultReceiver?
        ) {
            systemServiceScope.launchSafety {
                shortXContext.gesture.injectGestureRecord(
                    id = id,
                    speed = speed,
                    showGesturePathView = true,
                    onComplete = onComplete
                )
            }
        }

        override fun startGestureRecording() {
            shortXContext.gesture.startGestureRecording()
        }

        override fun stopGestureRecording(save: Boolean) {
            shortXContext.gesture.stopGestureRecording(save)
        }

        override fun isGestureRecording(): Boolean {
            return shortXContext.gesture.isGestureRecording()
        }

        override fun getAllStatusBarTileSettings(): List<ByteArrayWrapper> {
            return shortXContext.tile.getAllStatusBarTileSettings().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getStatusBarTileSettingsByNumber(tileNumber: Int): ByteArrayWrapper? {
            return shortXContext.tile.getStatusBarTileSettingsByNumber(tileNumber)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun updateStatusBarTileSetting(tileSetting: ByteArrayWrapper) {
            shortXContext.tile.updateStatusBarTileSetting(StatusBarTileSetting.parseFrom(tileSetting.byteData))
        }

        override fun registerStatusBarTileSettingsChangeListener(listener: IStatusBarTileSettingsChangeListener) {
            shortXContext.tile.registerStatusBarTileSettingsChangeListener(listener)
        }

        override fun unregisterStatusBarTileSettingsChangeListener(listener: IStatusBarTileSettingsChangeListener) {
            shortXContext.tile.unregisterStatusBarTileSettingsChangeListener(listener)
        }

        @SuppressLint("MissingPermission")
        override fun onTileClick(tileNumber: Int) {
            shortXContext.rule.publishFact(
                reason = "onTileClick-$tileNumber",
                fact = OnQSTileClick.newBuilder().setTileNumber(tileNumber).build(),
                initialContextData = emptyMap()
            )
            if (OsUtils.isQOrAbove()) {
                systemServiceScope.launchSafety {
                    context.statusBarManager.collapsePanels()
                }
            }
        }

        override fun isQSTileOptEnabled(): Boolean {
            return shortXContext.tile.isQSTileOptEnabled()
        }

        override fun setQSTileOptEnabled(enable: Boolean) {
            shortXContext.tile.setQSTileOptEnabled(enable)
        }

        override fun isSettingsCatcherEnabled(): Boolean {
            return shortXContext.systemSettings.isSettingsCatcherEnabled()
        }

        override fun setSettingsCatcherEnabled(enabled: Boolean) {
            shortXContext.systemSettings.setSettingsCatcherEnabled(enabled)
        }

        override fun onReadSettings(url: String, value: String) {
            shortXContext.systemSettings.onRead(url, value)
        }

        override fun onWriteSettings(url: String, value: String) {
            shortXContext.systemSettings.onWrite(url, value)
        }

        override fun getReadSettingsRecord(): List<ByteArrayWrapper> {
            return shortXContext.systemSettings.getReadSettingsRecord().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getWriteSettingsRecord(): List<ByteArrayWrapper> {
            return shortXContext.systemSettings.getWriteSettingsRecord().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun clearWriteSettingsRecord() {
            shortXContext.systemSettings.clearWriteSettingsRecord()
        }

        override fun clearReadSettingsRecord() {
            shortXContext.systemSettings.clearReadSettingsRecord()
        }

        override fun setRuleEngineEnabled(engine: String, enable: Boolean) {
            shortXContext.rule.setRuleEngineEnabled(engine, enable)
        }

        override fun isRuleEngineEnabled(engine: String): Boolean {
            return shortXContext.rule.isRuleEngineEnabled(engine)
        }

        override fun directExecuteRuleActions(evaluateContext: ByteArrayWrapper, ruleId: String) {
            shortXContext.rule.directExecuteRuleActions(
                context = EvaluateContext.parseFrom(evaluateContext.byteData),
                ruleId = ruleId
            )
        }

        override fun executeAction(evaluateContext: ByteArrayWrapper, action: ByteArrayWrapper) {
            enforceCallingPermissions()
            shortXContext.rule.executeActions(
                context = EvaluateContext.parseFrom(evaluateContext.byteData),
                actions = listOf(ProtoAny.parseFrom(action.byteData)),
                conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth
            )
        }

        override fun executeActionBlocking(
            evaluateContext: ByteArrayWrapper,
            action: ByteArrayWrapper
        ) {
            enforceCallingPermissions()
            shortXContext.rule.executeActionsBlocking(
                context = EvaluateContext.parseFrom(evaluateContext.byteData),
                actions = listOf(ProtoAny.parseFrom(action.byteData))
            )
        }

        override fun getActionEvaluateRecords(): List<ByteArrayWrapper> {
            return shortXContext.rule.getActionEvaluateRecords().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getEvaluatingActions(): List<String> {
            return shortXContext.rule.getEvaluatingActions()
        }

        override fun getConditionEvaluateRecords(): List<ByteArrayWrapper> {
            return shortXContext.rule.getConditionEvaluateRecords().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getFactPublishRecords(): List<ByteArrayWrapper> {
            return shortXContext.rule.getFactPublishRecords().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun clearEvaluateRecords() {
            shortXContext.rule.clearEvaluateRecords()
        }

        override fun isEvaluateRecordEnabled(): Boolean {
            return shortXContext.rule.isEvaluateRecordEnabled()
        }

        override fun setEvaluateRecordEnabled(enable: Boolean) {
            shortXContext.rule.setEvaluateRecordEnabled(enable)
        }

        override fun getGestureAreaSize(): Int {
            return shortXContext.gesture.getGestureAreaWidthDp()
        }

        override fun setGestureAreaSize(dp: Int) {
            shortXContext.gesture.setGestureAreaWidthDp(dp)
        }

        override fun registerPkgSetObs(obs: IPkgSetObserver) {
            shortXContext.pkgSet.registerPkgSetObs(obs)
        }

        override fun unregisterPkgSetObs(obs: IPkgSetObserver) {
            shortXContext.pkgSet.unregisterPkgSetObs(obs)
        }

        override fun registerRuleObs(obs: IRuleObserver) {
            shortXContext.rule.registerRuleObs(obs)
        }

        override fun unregisterRuleObs(obs: IRuleObserver) {
            shortXContext.rule.unregisterRuleObs(obs)
        }

        override fun registerDAObs(obs: IDAObserver) {
            shortXContext.rule.registerDAObs(obs)
        }

        override fun unregisterDAObs(obs: IDAObserver) {
            shortXContext.rule.unregisterDAObs(obs)
        }

        override fun setKeyEventPromptEnabled(enable: Boolean) {
            shortXContext.rule.setKeyEventPromptEnabled(enable)
        }

        override fun isKeyEventPromptEnabled(): Boolean {
            return shortXContext.rule.isKeyEventPromptEnabled()
        }

        override fun executeMVEL(expression: String): String {
            enforceCallingPermissions()
            return shortXContext.rule.executeMVEL(expression)
        }

        override fun executeJS(expression: String): String {
            enforceCallingPermissions()
            return shortXContext.rule.executeJS(expression)
        }

        override fun addOrUpdateRuleSet(rs: ByteArrayWrapper) {
            shortXContext.rule.addOrUpdateRuleSet(RuleSet.parseFrom(rs.byteData))
        }

        override fun deleteRuleSet(id: String) {
            shortXContext.rule.deleteRuleSet(id)
        }

        override fun getRuleSetById(id: String, withRules: Boolean): ByteArrayWrapper? {
            return shortXContext.rule.getRuleSetById(id, withRules)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getAllRuleSets(withRules: Boolean): List<ByteArrayWrapper> {
            return shortXContext.rule.getAllRuleSets(withRules).map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun addOrUpdateDASet(ds: ByteArrayWrapper) {
            shortXContext.rule.addOrUpdateDASet(DirectActionSet.parseFrom(ds.byteData))
        }

        override fun deleteDASet(id: String) {
            shortXContext.rule.deleteDASet(id)
        }

        override fun getDASetById(id: String, withDAs: Boolean): ByteArrayWrapper? {
            return shortXContext.rule.getDASetById(id, withDAs)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getAllDASets(withDAs: Boolean): List<ByteArrayWrapper> {
            return shortXContext.rule.getAllDASets().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun vb(code: String, deviceId: String, cb: ICallback) {
            systemServiceScope.launchSafety {
                tornaco.apps.shortx.services.v.vb(
                    code = code,
                    deviceId = deviceId,
                    onSuccess = {
                        kotlin.runCatching { cb.receive(ByteArrayWrapper(it.toByteArray())) }
                    },
                    onError = {})
            }
        }

        override fun getAllUsers(): List<ByteArrayWrapper> {
            return runClearCallingIdentity {
                shortXContext.user.getUsers().map {
                    ByteArrayWrapper(
                        UserInfo.newBuilder()
                            .setUserId(it.id)
                            .setName(it.name)
                            .setIsMain(it.id == UserHandle.USER_SYSTEM)
                            .build().toByteArray()
                    )
                }
            } ?: emptyList()
        }

        override fun setPointerLocationEnabled(enable: Boolean) {
            shortXContext.systemSettings.setPointerLocationEnabled(enable)
        }

        override fun isPointerLocationEnabled(): Boolean {
            return shortXContext.systemSettings.isPointerLocationEnabled()
        }

        override fun addPkgSet(set: ByteArrayWrapper) {
            shortXContext.pkgSet.addPkgSet(PkgSet.parseFrom(set.byteData))
        }

        override fun deletePkgSet(id: String) {
            shortXContext.pkgSet.deletePkgSet(id)
        }

        override fun getPkgSetByLabel(label: String): ByteArrayWrapper? {
            return shortXContext.pkgSet.getPkgSetByLabel(label)?.let {
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
                            .setDescription(it.description)
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
            shortXContext.ui.setDanmuUISettings(DanmuUISettings.parseFrom(settings.byteData))
        }

        override fun getAppIcon(pkgName: String, userId: Int): Bitmap? {
            return runClearCallingIdentity {
                val drawable: Drawable? = ApkUtils.loadIconByPkgName(context, pkgName)
                logger.d("getAppIcon, drawable: $drawable")
                drawable?.let {
                    val badged =
                        context.packageManager.getUserBadgedIcon(it, userIdToUserHandle(userId))
                    BitmapUtils.getBitmap(context, badged)
                }
            }
        }

        override fun getActiveJobs(): List<String> {
            return shortXContext.rule.getActiveJobs()
        }

        override fun cancelJobs(jobs: List<String>) {
            shortXContext.rule.cancelJobs(jobs)
        }

        override fun registerJobStatusListener(listener: IJobStatusListener) {
            shortXContext.rule.registerJobStatusListener(listener)
        }

        override fun unregisterJobStatusListener(listener: IJobStatusListener) {
            shortXContext.rule.unregisterJobStatusListener(listener)
        }

        override fun registerActionEvaluateListener(listener: IActionEvaluateListener) {
            shortXContext.rule.registerActionEvaluateListener(listener)
        }

        override fun unregisterActionEvaluateListener(listener: IActionEvaluateListener) {
            shortXContext.rule.unregisterActionEvaluateListener(listener)
        }

        override fun setStartActivityResult(requestCode: Int, resultCode: Int, result: Bundle) {
            shortXContext.appState.setStartActivityResult(requestCode, resultCode, result)
        }

        override fun shortcutAppLaunched(packageName: String) {
            shortXContext.rule.shortcutAppLaunched(packageName)
        }

        override fun setRuleFeatureEnabled(enable: Boolean) {
            enforceCallingPermissions()
            shortXContext.rule.setRuleFeatureEnabled(enable)
        }

        override fun isRuleFeatureEnabled(): Boolean {
            return shortXContext.rule.isRuleFeatureEnabled()
        }

        override fun querySensorUsages(): List<ByteArrayWrapper> {
            return shortXContext.watchDog.querySensorUsage().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun registerSensorUsageListener(listener: ISensorUsageListener) {
            shortXContext.watchDog.registerSensorUsageListener(listener)
        }

        override fun unregisterSensorUsageListener(listener: ISensorUsageListener) {
            shortXContext.watchDog.unregisterSensorUsageListener(listener)
        }

        override fun setPostNByAppEnabled(enable: Boolean) {
            shortXContext.notification.setPostNByAppEnabled(enable)
        }

        override fun isPostNByAppEnabled(): Boolean {
            return shortXContext.notification.isPostNByAppEnabled()
        }

        override fun installApk(
            packageName: String,
            fd: ParcelFileDescriptor,
            onSuccess: ICallback,
            onFailure: ICallback
        ) {
            enforceCallingPermissions()
            shortXContext.appState.installApk(
                packageName = packageName,
                fd = fd,
                onSuccess = {
                    onSuccess.receive(emptyByteArrayWrapper)
                },
                onFailure = {
                    onFailure.receive(emptyByteArrayWrapper)
                })
        }

        override fun getInstalledShortcutApps(): List<ByteArrayWrapper> {
            return shortXContext.appState.getInstalledApps().filter {
                it.isShortcutStub
            }.map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun setHttpRequestLogEnabled(enabled: Boolean) {
            shortXContext.rule.setHttpRequestLogEnabled(enabled)
        }

        override fun isHttpRequestLogEnabled(): Boolean {
            return shortXContext.rule.isHttpRequestLogEnabled()
        }

        override fun getHttpRequestLogFD(): ParcelFileDescriptor? {
            return shortXContext.rule.getHttpRequestLogFD()
        }

        override fun getHttpRequestLogPath(): String {
            return shortXContext.rule.getHttpRequestLogPath()
        }

        override fun clearHttpRequestLogs() {
            shortXContext.rule.clearHttpRequestLogs()
        }

        override fun addToggle(toggle: ByteArrayWrapper) {
            enforceCallingPermissions()
            v(context) {
                shortXContext.rule.addToggle(Toggle.parseFrom(toggle.byteData))
            }
        }

        override fun deleteToggle(id: String) {
            shortXContext.rule.deleteToggle(id)
        }

        override fun getToggleById(id: String): ByteArrayWrapper? {
            return shortXContext.rule.getToggleById(id)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getAllToggles(): List<ByteArrayWrapper> {
            return shortXContext.rule.getAllToggles().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getToggleCount(): Int {
            return shortXContext.rule.getToggleCount()
        }

        override fun registerToggleObs(obs: IToggleObserver) {
            shortXContext.rule.registerToggleObs(obs)
        }

        override fun unregisterToggleObs(obs: IToggleObserver) {
            shortXContext.rule.unregisterToggleObs(obs)
        }

        override fun getActivities(
            userId: Int,
            packageName: String
        ): List<ByteArrayWrapper> {
            return shortXContext.appState.getActivities(userId, packageName).map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getReceivers(
            userId: Int,
            packageName: String
        ): List<ByteArrayWrapper> {
            return shortXContext.appState.getReceivers(userId, packageName).map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getServices(userId: Int, packageName: String): List<ByteArrayWrapper> {
            return shortXContext.appState.getServices(userId, packageName).map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getProviders(
            userId: Int,
            packageName: String
        ): List<ByteArrayWrapper> {
            return shortXContext.appState.getProviders(userId, packageName).map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun setComponentEnabledSetting(
            component: ByteArrayWrapper,
            newState: Int,
            flags: Int
        ) {
            shortXContext.appState.setComponentEnabledSetting(
                tornaco.apps.shortx.core.proto.common.AppComponent.parseFrom(component.byteData),
                newState, flags
            )
        }

        override fun getComponentEnabledSetting(component: ByteArrayWrapper): Int {
            return shortXContext.appState.getComponentEnabledSetting(
                tornaco.apps.shortx.core.proto.common.AppComponent.parseFrom(component.byteData)
            )
        }

        override fun getAppComponentInfo(component: ByteArrayWrapper): ByteArrayWrapper? {
            return shortXContext.appState.getAppComponentInfo(
                tornaco.apps.shortx.core.proto.common.AppComponent.parseFrom(
                    component.byteData
                )
            )?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun addGlobalVar(gv: ByteArrayWrapper) {
            shortXContext.rule.addGlobalVar(GlobalVar.parseFrom(gv.byteData))
        }

        override fun deleteGlobalVar(id: String) {
            shortXContext.rule.deleteGlobalVar(id)
        }

        override fun getGlobalVarByName(id: String): ByteArrayWrapper? {
            return shortXContext.rule.getGlobalVarByName(id)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getAllGlobalVars(): List<ByteArrayWrapper> {
            return shortXContext.rule.getAllGlobalVars().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun registerGlobalVarObs(obs: IGlobalVarObserver) {
            shortXContext.rule.registerGlobalVarObs(obs)
        }

        override fun unregisterGlobalVarObs(obs: IGlobalVarObserver) {
            shortXContext.rule.unregisterGlobalVarObs(obs)
        }

        override fun getRunningAppPkgs(pkgSetLabel: String): List<ByteArrayWrapper> {
            return shortXContext.appState.getRunningAppPkgs(pkgSetLabel)
                .map { ByteArrayWrapper(it.toByteArray()) }
        }

        override fun getRunningServices(pkg: ByteArrayWrapper): List<ByteArrayWrapper> {
            return shortXContext.appState.getRunningServices(AppPkg.parseFrom(pkg.byteData))
                .map { ByteArrayWrapper(it.toByteArray()) }
        }

        @SuppressLint("MissingPermission")
        override fun reboot(reason: String?) {
            logger.w("reboot: $reason")
            handler.postSafety {
                context.powerManager.reboot(null)
            }
        }
    }
}