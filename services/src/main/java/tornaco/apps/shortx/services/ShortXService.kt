package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Binder
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.ServiceManager
import android.os.UserHandle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.*
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AudioRecordFile
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.proto.common.UserInfo
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.IPkgSetObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.util.ApkUtils
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.UserHandleUtils.userIdToUserHandle
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.core.util.wifiManager
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequest.toHttpRequestBody
import tornaco.apps.shortx.services.rule.fact.trigger.BTHelper
import tornaco.apps.shortx.services.ui.I18NCache
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.v.v
import java.io.File
import java.util.UUID

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
                shortXContext.rule.addOrUpdateRule(Rule.parseFrom(rule.byte))
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
            enforceCallingPermissions()
            v(context) {
                shortXContext.rule.addDirectAction(DirectAction.parseFrom(da.byte))
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

        override fun executeDirectionActionById(id: String) {
            enforceCallingPermissions()
            shortXContext.rule.executeDirectionActionById(id)
        }

        override fun executeDirectionAction(da: ByteArrayWrapper) {
            enforceCallingPermissions()
            shortXContext.rule.executeDirectionAction(DirectAction.parseFrom(da.byte))
        }

        override fun onMediaStoreInsert(uri: Uri, path: String) {
            shortXContext.media.onMediaStoreInsert(uri, path)
        }

        override fun onMediaStoreDelete(uri: Uri, path: String) {
            shortXContext.media.onMediaStoreDelete(uri, path)
        }

        override fun executeShellCommand(command: String): ByteArrayWrapper {
            enforceCallingPermissions()
            logger.d("executeShellCommand: $command")
            val res = shortXContext.shell.executeCommand(command) ?: ShellRes.newBuilder().build()
            return ByteArrayWrapper(res.toByteArray())
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
            shortXContext.audio.deleteAudioRecordFile(AudioRecordFile.parseFrom(file.byte))
        }

        override fun exportAudioRecordFile(file: ByteArrayWrapper, pfd: ParcelFileDescriptor?) {
            logger.w("exportAudioRecordFile: $file $pfd")
            shortXContext.audio.exportAudioRecordFile(
                AudioRecordFile.parseFrom(file.byte),
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
                shortXContext.appState.uidToApp(uid)
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

        override fun executeHttpRequest(request: ByteArrayWrapper): List<String> {
            val data = HttpRequest.parseFrom(request.byte)
            val url = data.url.replaceGlobalVarValues {
                shortXContext.rule.getAllGlobalVars()
            }
            val requestBody =
                data.requestBody.toHttpRequestBody(ContextData.obtain(), shortXContext)
            return kotlin.runCatching {
                tornaco.apps.shortx.services.rule.actor.impl.HttpRequest.handleUrl(
                    url = url,
                    method = data.method.name,
                    headers = data.headersList.associate {
                        it.key to (it.value).replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
                    },
                    requestBody = requestBody,
                    adapter = data.adapter
                )
            }.getOrElse {
                listOf(Log.getStackTraceString(it))
            }
        }

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

        override fun directExecuteRuleActions(ruleId: String) {
            shortXContext.rule.directExecuteRuleActions(ruleId)
        }

        override fun executeAction(action: ByteArrayWrapper) {
            enforceCallingPermissions()
            shortXContext.rule.executeActions(
                id = "EA-${UUID.randomUUID()}",
                reason = "IShortX executeAction",
                actions = listOf(ProtoAny.parseFrom(action.byte)),
                initialContextData = mapOf(
                    ContextDataMapping.Context.contextSrcTitle.name to "Direct execute",
                    ContextDataMapping.Context.contextSrcEvent.name to ContextSrcEvent.ContextSrc_ExecuteOnDAEditor,
                )
            )
        }

        override fun executeActionBlocking(action: ByteArrayWrapper) {
            enforceCallingPermissions()
            shortXContext.rule.executeActionsBlocking(
                "EAB-${UUID.randomUUID()}",
                "IShortX executeAction",
                listOf(ProtoAny.parseFrom(action.byte))
            )
        }

        override fun getActionEvaluateRecords(): List<ByteArrayWrapper> {
            return shortXContext.rule.getActionEvaluateRecords().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun clearActionEvaluateRecords() {
            shortXContext.rule.clearActionEvaluateRecords()
        }

        override fun setActionEvaluateRecorderEnabled(enable: Boolean) {
            shortXContext.rule.setActionEvaluateRecorderEnabled(enable)
        }

        override fun isActionEvaluateRecorderEnabled(): Boolean {
            return shortXContext.rule.isActionEvaluateRecorderEnabled()
        }

        override fun getConditionEvaluateRecords(): List<ByteArrayWrapper> {
            return shortXContext.rule.getConditionEvaluateRecords().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun clearConditionEvaluateRecords() {
            shortXContext.rule.clearConditionEvaluateRecords()
        }

        override fun getFactPublishRecords(): List<ByteArrayWrapper> {
            return shortXContext.rule.getFactPublishRecords().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun clearFactPublishRecords() {
            shortXContext.rule.clearFactPublishRecords()
        }

        override fun setFactPublishRecordEnabled(enable: Boolean) {
            shortXContext.rule.setFactPublishRecordEnabled(enable)
        }

        override fun isFactPublishRecordEnabled(): Boolean {
            return shortXContext.rule.isFactPublishRecordEnabled()
        }

        override fun getGestureAreaSize(): Int {
            return shortXContext.gesture.getGestureAreaSize()
        }

        override fun setGestureAreaSize(dp: Int) {
            shortXContext.gesture.setGestureAreaSize(dp)
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

        override fun setConditionEvaluateRecorderEnabled(enable: Boolean) {
            shortXContext.rule.setConditionEvaluateRecorderEnabled(enable)
        }

        override fun isConditionEvaluateRecorderEnabled(): Boolean {
            return shortXContext.rule.isConditionEvaluateRecorderEnabled()
        }

        override fun executeMVEL(expression: String): String {
            return shortXContext.rule.executeMVEL(expression)
        }

        override fun evaluateCondition(condition: ByteArrayWrapper): Boolean {
            return shortXContext.rule.evaluateCondition(ProtoAny.parseFrom(condition.byte))
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
            shortXContext.pkgSet.addPkgSet(PkgSet.parseFrom(set.byte))
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
                tornaco.apps.shortx.core.proto.common.AppComponent.parseFrom(component.byte),
                newState, flags
            )
        }

        override fun getComponentEnabledSetting(component: ByteArrayWrapper): Int {
            return shortXContext.appState.getComponentEnabledSetting(
                tornaco.apps.shortx.core.proto.common.AppComponent.parseFrom(component.byte)
            )
        }

        override fun getAppComponentInfo(component: ByteArrayWrapper): ByteArrayWrapper? {
            return shortXContext.appState.getAppComponentInfo(
                tornaco.apps.shortx.core.proto.common.AppComponent.parseFrom(
                    component.byte
                )
            )?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun addGlobalVar(gv: ByteArrayWrapper) {
            shortXContext.rule.addGlobalVar(GlobalVar.parseFrom(gv.byte))
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
            return shortXContext.appState.getRunningServices(AppPkg.parseFrom(pkg.byte))
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