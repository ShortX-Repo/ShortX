package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.pm.ShortcutInfo
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.ShellCommand
import android.os.UserHandle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import android.view.MotionEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.AppGlobals
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.IAudioRecordingListener
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.INFCTagEndpointListener
import tornaco.apps.shortx.core.ISensorUsageListener
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.SystemUIClient
import tornaco.apps.shortx.core.Version
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.cv.ICV
import tornaco.apps.shortx.core.http.toHttpRequestBody
import tornaco.apps.shortx.core.http.toHttpResponseData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.media.FileRow
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.ocr.IOCR
import tornaco.apps.shortx.core.os.ICancellationSignal
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.phone.ISMSProvider
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AudioRecordFile
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.proto.common.CustomContextMenuSetting
import tornaco.apps.shortx.core.proto.common.DynamicShortcutSetting
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.RemoteRuleContextAndVars
import tornaco.apps.shortx.core.proto.common.ShortXServerDirs
import tornaco.apps.shortx.core.proto.common.StatusBarClipUiSettings
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.common.UserInfo
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.fact.DeepLinkCall
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.MethodHook
import tornaco.apps.shortx.core.proto.fact.ScreenGesture
import tornaco.apps.shortx.core.proto.feature.AppFeature
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.IPkgSetObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.IToggleObserver
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.emptyByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.rule.action.wrap
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.systemui.IQSTileHost
import tornaco.apps.shortx.core.util.ApkUtils
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.UserHandleUtils.userIdToUserHandle
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.core.util.wifiManager
import tornaco.apps.shortx.services.abtesting.ABTesting
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequest.handleResponseBody
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequestLogging
import tornaco.apps.shortx.services.rule.fact.trigger.BTHelper
import tornaco.apps.shortx.services.rule.nextEvaluateContextSerialNumber
import tornaco.apps.shortx.services.rule.systemenv.SystemEnvInit
import tornaco.apps.shortx.services.ui.I18NCache
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.util.ListBatchUtils
import tornaco.apps.shortx.services.util.ListBatchUtils.Batch
import tornaco.apps.shortx.services.util.MethodHookLogging
import tornaco.apps.shortx.services.util.waitUntil
import tornaco.apps.shortx.services.v.v
import tornaco.apps.shortx.services.watchdog.SystemServerCrashHandler
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import java.io.File

@SuppressLint("StaticFieldLeak")
object ShortXService : SystemService(ShortXContextImpl(), "ShortXService") {
    private val errorGetAllDARes = listOf(
        ByteArrayWrapper(
            DirectAction.newBuilder()
                .setId("ERROR-GET-DA")
                .setCreateTime(System.currentTimeMillis())
                .setTitle("ERROR")
                .setDescription("ERROR!!! please file a bug")
                .setAuthor(Author.newBuilder().setName("ShortX").build())
                .build()
                .toByteArray()
        )
    )

    private var systemUIClient: SystemUIClient? = null

    fun main() {
        LogInit.initEarlyLogging()
        ShortXManagerNative.attachLocalService(serviceBinder)
    }

    override fun start(context: Context) {
        super.start(context)
        AppGlobals.setContext(context)
        LogInit.initStartedLogging()
        shortXContext.serviceLifecycle.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        SystemServerCrashHandler.install()
        //  publishBinderService()
        // Init i18N
        val appRes = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
        AppGlobals.setAppResources(appRes)
        val localeName = AppGlobals.appResources.getString("ui_i18n_name")
        I18NCache.init(AppGlobals.appContext, localeName)
        val testRes = i18NIns["ui.common.delete"]
        logger.d("testRes: $testRes")
        shortXContext.serviceLifecycle.systemReady()
        ABTesting.dump()
        waitForSystemUIClient()
    }

    private fun waitForSystemUIClient() {
        shortXContext.serviceLifecycle.registerLifecycleSystemReadyCallback(object :
            ICallback.Stub() {
            override fun receive(p0: ByteArrayWrapper?) {
                val tag = "waitForSystemUIClient"
                systemServiceScope.launchSafety(tag) {
                    val hasSystemUIClient = waitUntil(60 * 1000) {
                        logger.w("Check systemUIClient at the $it time.")
                        systemUIClient != null
                    }
                    logger.w("waitForSystemUIClient hasSystemUIClient: $hasSystemUIClient")
                    if (!hasSystemUIClient) {
                        shortXContext.notification.postByFallback(
                            PostNotification.newBuilder()
                                .setTag(tag)
                                .setTitle(i18NIns["ui.n.wait.for.systemui.title"])
                                .setMessage(i18NIns["ui.n.wait.for.systemui.message"])
                                .setId(tag)
                                .setSmallIcon(Remix.System.error_warning_line)
                                .setIsImportant(true)
                                .setVibrate(true)
                                .build()
                        )
                    }

                    publishSystemReadyFact()
                }
            }
        })
    }

    private fun publishSystemReadyFact() {
        systemServiceScope.launchSafety("publishSystemReadyFact") {
            logger.i("About to publishSystemReadyFact")
            delay(3000)
            shortXContext.user.publishSystemReady()
        }
    }

    fun onCommand(shellCommand: ShellCommand): Int {
        enforceCallingPermissions()

        val pw = shellCommand.outPrintWriter
        val nextArgs: String? = shellCommand.nextArg
        if (nextArgs == null) {
            pw.println("Welcome to ShortX(${BuildPropInternal.APP_VERSION_NAME}). Command usage:")
            pw.println("shortx run [ID] [arg1]=[value] [arg2]=[value] ...")
            pw.println("Example: shortx run 7da392dxfjysl size=24 color=#ffffff")
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

            val args = mutableListOf<FuncParameterInput>()
            var nextArgParam = shellCommand.nextArg
            while (nextArgParam != null) {
                val kv = nextArgParam.split("=")
                args.add(FuncParameterInput.newBuilder().setName(kv[0]).setValue(kv[1]).build())
                nextArgParam = shellCommand.nextArg
            }
            pw.println("args: ${args.map { it.toLogString() }}")

            shortXContext.rule.executeDirectionActionById(
                context = EvaluateContext.newBuilder()
                    .setSrcId(id)
                    .setSrcTitle("Shell command")
                    .setSrcEvent(ContextSrcEvent.ContextSrc_DirectAction)
                    .setSerialNumber(nextEvaluateContextSerialNumber())
                    .build(),
                id = id,
                parameterInputs = args
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
        private val directActionQueryCache =
            mutableMapOf("Dummy" to Batch<DirectAction>(emptyList()))
        private val ruleQueryCache =
            mutableMapOf("Dummy" to Batch<Rule>(emptyList()))

        override fun version(): Version {
            return Version(
                name = BuildPropInternal.APP_VERSION_NAME,
                code = BuildPropInternal.APP_VERSION_CODE
            )
        }

        override fun fingerprint() = BuildPropInternal.BUILD_FINGERPRINT

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
            enforceCallingPermissions()
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

        override fun getAllRules(
            queryId: String,
            pageNumber: Int,
            pageSize: Int
        ): List<ByteArrayWrapper> {
            logger.d("getAllRules-$queryId")
            return v(context) {
                synchronized(ruleQueryCache) {
                    val cache = ruleQueryCache[queryId]
                    // Keep only one.
                    if (cache == null) {
                        ruleQueryCache.clear()
                    } else {
                        logger.d("Use ruleQueryCache")
                    }
                    val batches =
                        cache ?: ListBatchUtils.toArrayBatch(
                            shortXContext.rule.getAllRules(),
                            pageSize
                        ).also {
                            ruleQueryCache[queryId] = it
                        }

                    val totalBatchCount: Int = batches.batches.size
                    if (pageNumber >= totalBatchCount) {
                        emptyList()
                    } else batches.batches[pageNumber].map {
                        ByteArrayWrapper(it.toByteArray())
                    }
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

        override fun showEdgeHint(edgeNumber: IntArray) {
            shortXContext.gesture.showEdgeHint(edgeNumber.toList())
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

        override fun getAllDirectAction(
            queryId: String?,
            pageNumber: Int,
            pageSize: Int
        ): List<ByteArrayWrapper> {
            logger.d("getAllDirectAction queryId:$queryId pageNumber: $pageNumber pageSize: $pageSize")
            val res =
                v(context) {
                    if (queryId != null) {
                        getAllDirectActionWithQueryCache(
                            queryId,
                            pageSize,
                            pageNumber
                        )
                    } else {
                        getAllDirectAction(pageSize, pageNumber)
                    }
                }
            val byteSize = res?.sumOf { it.byteData.size }
            logger.d("getAllDirectAction, returning res size: ${res?.size}, byteSize: $byteSize")

            return res ?: emptyList()
        }

        private fun getAllDirectAction(
            pageSize: Int,
            pageNumber: Int
        ): List<ByteArrayWrapper> {
            return kotlin.runCatching {
                val batches =
                    ListBatchUtils.toArrayBatch(
                        shortXContext.rule.getAllDirectAction(),
                        pageSize
                    )
                val totalBatchCount: Int = batches.batches.size
                logger.d("getAllDirectAction totalBatchCount- $totalBatchCount")
                logger.d("getAllDirectAction totalBatchCount total - ${batches.batches.sumOf { it.size }}")

                if (pageNumber >= totalBatchCount) {
                    emptyList()
                } else batches.batches[pageNumber].mapNotNull { directAction ->
                    runCatching { ByteArrayWrapper(directAction.toByteArray()) }.getOrElse {
                        logger.e(it, "Map one DA error: $directAction")
                        null
                    }
                }
            }.getOrElse {
                shortXContext.watchDog.onFatalError(it, "getAllDirectAction")
                errorGetAllDARes
            }
        }

        private fun getAllDirectActionWithQueryCache(
            queryId: String,
            pageSize: Int,
            pageNumber: Int
        ): List<ByteArrayWrapper> {
            return kotlin.runCatching {
                synchronized(directActionQueryCache) {
                    val cache = directActionQueryCache[queryId]
                    // Keep only one.
                    if (cache == null) {
                        directActionQueryCache.clear()
                    } else {
                        logger.d("getAllDirectActionWithQueryCache Use directActionQueryCache")
                    }
                    val batches =
                        cache ?: ListBatchUtils.toArrayBatch(
                            shortXContext.rule.getAllDirectAction(),
                            pageSize
                        ).also {
                            directActionQueryCache[queryId] = it
                        }

                    val totalBatchCount: Int = batches.batches.size
                    logger.d("getAllDirectActionWithQueryCache totalBatchCount- $totalBatchCount")
                    logger.d("getAllDirectActionWithQueryCache totalBatchCount total - ${batches.batches.sumOf { it.size }}")

                    if (pageNumber >= totalBatchCount) {
                        emptyList()
                    } else batches.batches[pageNumber].mapNotNull { directAction ->
                        runCatching { ByteArrayWrapper(directAction.toByteArray()) }.getOrElse {
                            logger.e(it, "Map one DA error: $directAction")
                            null
                        }
                    }
                }
            }.getOrElse {
                shortXContext.watchDog.onFatalError(it, "getAllDirectAction")
                errorGetAllDARes
            }
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
                id = id,
                parameterInputs = emptyList()
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

        override fun onMediaStoreInsert(row: FileRow) {
            logger.w("onMediaStoreInsert. ${row.id} ${row.mediaUri}")
            shortXContext.media.onMediaStoreInsert(row)
        }

        override fun onMediaStoreDelete(row: FileRow) {
            shortXContext.media.onMediaStoreDelete(row)
        }

        override fun executeShellCommand(
            command: String,
            callback: ICallback
        ): ICancellationSignal {
            enforceCallingPermissions()
            logger.d("executeShellCommand: $command")
            return shortXContext.rule.executeShell(command, callback)
        }

        @SuppressLint("MissingPermission")
        override fun getWifiScanResults(): List<String> {
            enforceCallingPermissions()
            return v(context) {
                runClearCallingIdentity {
                    context.wifiManager.scanResults.mapNotNull { it.SSID }
                }
            } ?: emptyList()
        }

        override fun getPrivilegedConfiguredNetworks(): List<ByteArrayWrapper> {
            enforceCallingPermissions()
            return shortXContext.network.getPrivilegedConfiguredNetworks().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun deepLinkTriggerCalled(tag: String) {
            enforceCallingPermissions()
            shortXContext.rule.publishFact(
                "deepLinkTriggerCalled",
                DeepLinkCall.newBuilder().setDeepLinkTag(tag).build()
            ) {
                emptyMap()
            }
        }

        override fun getAllRingtones(): List<ByteArrayWrapper> {
            return shortXContext.audio.getAllRingtones().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getMaxSignalLevel(): Int {
            return shortXContext.network.getMaxSignalLevel()
        }

        override fun setForceDisableShortXByKeyPressEnabled(enable: Boolean) {
            enforceCallingPermissions()
            shortXContext.rule.setForceDisableShortXByKeyPressEnabled(enable)
        }

        override fun isForceDisableShortXByKeyPressEnabled(): Boolean {
            return shortXContext.rule.isForceDisableShortXByKeyPressEnabled()
        }

        override fun dispatchTagEndpoint(uid: ByteArray) {
            shortXContext.phone.dispatchTagEndpoint(uid)
        }

        override fun registerNFCTagEndpointListener(listener: INFCTagEndpointListener) {
            shortXContext.phone.registerNFCTagEndpointListener(listener)
        }

        override fun unregisterNFCTagEndpointListener(listener: INFCTagEndpointListener) {
            shortXContext.phone.unregisterNFCTagEndpointListener(listener)
        }

        override fun getSwipeLengthScale(): Float {
            return shortXContext.gesture.getGestureLengthScale()
        }

        override fun setSwipeLengthScale(scale: Float) {
            shortXContext.gesture.setGestureLengthScale(scale)
        }

        override fun onHookedMethodCalled(
            hookData: ByteArrayWrapper,
            argsData: MutableList<ByteArrayWrapper>
        ) {
            val hook = MethodHook.parseFrom(hookData.byteData)
            val args = argsData.map { StringPair.parseFrom(it.byteData) }
            shortXContext.rule.publishFact(
                "App hook",
                MethodHook.newBuilder(hook).build()
            ) {
                args.associate { it.first to it.second }
            }
        }

        override fun getMethodHookLogFD(): ParcelFileDescriptor? {
            return kotlin.runCatching {
                ParcelFileDescriptor.open(
                    MethodHookLogging.logFile,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            }.getOrElse {
                logger.e(it, "getMethodHookLogFD error")
                null
            }
        }

        override fun getMethodHookLogPath(): String {
            return MethodHookLogging.logFile.absolutePath
        }

        override fun clearMethodHookLogs() {
            MethodHookLogging.clearLogs()
        }

        override fun setKeyDetectorLongPressTimeoutMS(p0: Long) {
            shortXContext.gesture.setKeyDetectorLongPressTimeoutMS(p0.toInt())
        }

        override fun getKeyDetectorLongPressTimeoutMS(): Long {
            return shortXContext.gesture.getKeyDetectorLongPressTimeoutMS().toLong()
        }

        override fun getInstalledPlugins(): List<ByteArrayWrapper> {
            return shortXContext.plugin.getInstalledPlugins().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getInstalledPluginActions(): List<ByteArrayWrapper> {
            return shortXContext.plugin.getInstalledPluginActions().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getInstalledPluginConditions(): List<ByteArrayWrapper> {
            TODO("Not yet implemented")
        }

        override fun getInstalledPluginFacts(): List<ByteArrayWrapper> {
            TODO("Not yet implemented")
        }

        @Deprecated("Use SystemUIClient", ReplaceWith("SystemUIClient"))
        override fun attachCV(cv: ICV) {
            error("deprecated")
        }

        @Deprecated("Use SystemUIClient", ReplaceWith("SystemUIClient"))
        override fun attachOCR(ocr: IOCR) {
            error("deprecated")
        }

        override fun getRequestPinShortcuts(): List<ShortcutInfo> {
            return shortXContext.user.requestPinShortcuts

        }

        override fun getRequestPinAppWidgets(): List<AppWidgetProviderInfo> {
            return shortXContext.user.requestPinAppWidgets
        }

        override fun registerStatusbarClipStateListener(callback: ICallback) {
            shortXContext.statusbar.registerStatusbarClipStateListener(callback)
        }

        override fun unregisterStatusbarClipStateListener(callback: ICallback) {
            shortXContext.statusbar.unregisterStatusbarClipStateListener(callback)
        }

        override fun registerStatusbarClipSettingsListener(callback: ICallback) {
            shortXContext.statusbar.registerStatusbarClipSettingsListener(callback)
        }

        override fun unregisterStatusbarClipSettingsListener(callback: ICallback) {
            shortXContext.statusbar.unregisterStatusbarClipSettingsListener(callback)
        }

        override fun getCurrentStatusbarClipState(): ByteArrayWrapper {
            return shortXContext.statusbar.getCurrentStatusbarClipState().toByteArray().wrap()
        }

        override fun getStatusbarClipSettings(): ByteArrayWrapper {
            return shortXContext.statusbar.getStatusbarClipSettings().toByteArray().wrap()
        }

        override fun setStatusbarClipSettings(data: ByteArrayWrapper) {
            shortXContext.statusbar.setStatusbarClipSettings(StatusBarClipUiSettings.parseFrom(data.byteData))
        }

        override fun onStatusBarClipClick(id: String) {
            shortXContext.statusbar.onStatusBarClipClick(id)
        }

        override fun onStatusBarClipLongClick(id: String) {
            shortXContext.statusbar.onStatusBarClipLongClick(id)
        }

        override fun attachSMSProvider(provider: ISMSProvider) {
            shortXContext.phone.attachSMSProvider(provider)
        }

        @Deprecated("Use SystemUIClient", ReplaceWith("SystemUIClient"))
        override fun attachSUImpl(from: String?, su: ISu?) {
            error("deprecated")
        }

        @Deprecated("Use SystemUIClient", ReplaceWith("SystemUIClient"))
        override fun attachQSTileHost(host: IQSTileHost) {
            error("deprecated")
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

        override fun log(message: String?, redirectTo: String?) {
            if (redirectTo.isNullOrBlank()) {
                logger.i("EXTERNAL: $message")
            } else if ("http" == redirectTo) {
                message?.let { HttpRequestLogging.log(it) }
                logger.logOnDebug("http: $message")
            } else if ("method_hook" == redirectTo) {
                message?.let { MethodHookLogging.log(it) }
                logger.d("method_hook: $message")
            }
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

        override fun renameAudioRecordFile(file: ByteArrayWrapper, name: String) {
            shortXContext.audio.renameAudioRecordFile(
                AudioRecordFile.parseFrom(file.byteData),
                name
            )
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

        @Deprecated("Use SystemUIClient", ReplaceWith("SystemUIClient"))
        override fun attachNotificationPoster(from: String, poster: INotificationPoster) {
            error("deprecated")
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
            logFile.parentFile?.mkdirs()
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
                    RuleContextAndVars(
                        localVars,
                        ContextData.obtain(),
                    ) {
                        shortXContext.rule.getAllGlobalVars()
                    }
                )
            return kotlin.runCatching {
                val callerImpl =
                    tornaco.apps.shortx.services.rule.actor.impl.HttpRequest.localCallerImpl
                callerImpl.callUrl(
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
                        RuleContextAndVars(
                            contextData = ContextData.obtain(),
                            globalVarsLazy = { shortXContext.rule.getAllGlobalVars() },
                            localVars = LocalVars.obtain()
                        ),
                        response = it.toHttpResponseData(),
                        adapter = data.adapter
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
                systemServiceScope.launchSafety("getActiveSubscriptionInfoList") {
                    @Suppress("DEPRECATION")
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
            systemServiceScope.launchSafety("injectGestureRecord") {
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
            return shortXContext.statusbar.getAllStatusBarTileSettings().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getStatusBarTileSettingsByNumber(tileNumber: Int): ByteArrayWrapper? {
            return shortXContext.statusbar.getStatusBarTileSettingsByNumber(tileNumber)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun updateStatusBarTileSetting(tileSetting: ByteArrayWrapper) {
            shortXContext.statusbar.updateStatusBarTileSetting(
                StatusBarTileSetting.parseFrom(
                    tileSetting.byteData
                )
            )
        }

        override fun registerStatusBarTileSettingsChangeListener(listener: IStatusBarTileSettingsChangeListener) {
            shortXContext.statusbar.registerStatusBarTileSettingsChangeListener(listener)
        }

        override fun unregisterStatusBarTileSettingsChangeListener(listener: IStatusBarTileSettingsChangeListener) {
            shortXContext.statusbar.unregisterStatusBarTileSettingsChangeListener(listener)
        }

        override fun onTileClick(tileNumber: Int) {
            shortXContext.statusbar.onTileClick(tileNumber)
        }

        override fun isQSTileOptEnabled(): Boolean {
            return shortXContext.statusbar.isQSTileOptEnabled()
        }

        override fun setQSTileOptEnabled(enable: Boolean) {
            shortXContext.statusbar.setQSTileOptEnabled(enable)
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

        override fun executeAction(
            evaluateContext: ByteArrayWrapper,
            action: ByteArrayWrapper,
            contextAndVars: ByteArrayWrapper,
        ) {
            enforceCallingPermissions()
            val cv = RemoteRuleContextAndVars.parseFrom(contextAndVars.byteData)
            shortXContext.rule.executeActions(
                context = EvaluateContext.parseFrom(evaluateContext.byteData),
                actions = listOf(ProtoAny.parseFrom(action.byteData)),
                localVars = LocalVars.obtain().apply { addAll(cv.localVarList) },
                parameterInputs = cv.funcParameterList,
                initialContextData = cv.contextDataList.associate { it.first to it.second },
                conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
            )
        }

        override fun executeDirectionAction(
            evaluateContext: ByteArrayWrapper,
            da: ByteArrayWrapper,
            contextAndVars: ByteArrayWrapper,
        ) {
            enforceCallingPermissions()
            val cv = RemoteRuleContextAndVars.parseFrom(contextAndVars.byteData)
            val directAction = DirectAction.parseFrom(da.byteData)
            shortXContext.rule.executeDirectAction(
                context = EvaluateContext.parseFrom(evaluateContext.byteData),
                directAction = directAction,
                localVars = LocalVars.obtain().apply { addAll(cv.localVarList) },
                parameterInputs = cv.funcParameterList,
                initialContextData = cv.contextDataList.associate { it.first to it.second },
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

        override fun isEvaluateRecordEnabled(id: String): Boolean {
            return shortXContext.rule.isEvaluateRecordEnabled(id)
        }

        override fun setEvaluateRecordEnabled(id: String, enable: Boolean) {
            shortXContext.rule.setEvaluateRecordEnabled(id, enable)
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

        override fun executeMVEL(expression: String, context: Int): String {
            enforceCallingPermissions()
            return shortXContext.rule.executeMVEL(expression, CoroutineContext.forNumber(context))
        }

        override fun executeJS(expression: String, context: Int): String {
            enforceCallingPermissions()
            return shortXContext.rule.executeJS(expression, CoroutineContext.forNumber(context))
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

        override fun loadOnlineRules(code: String, deviceId: String, cb: ICallback) {
            systemServiceScope.launchSafety("loadOnlineRules") {
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
            shortXContext.rule.cancelJobs(jobs, "User requested(用户主动取消)")
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

        override fun getJSLogFD(): ParcelFileDescriptor? {
            return shortXContext.rule.getJSLogFD()
        }

        override fun getJSLogPath(): String {
            return shortXContext.rule.getJSLogPath()
        }

        override fun clearJSLogs() {
            shortXContext.rule.clearJSLogs()
        }

        override fun addWebDavProfile(profile: ByteArrayWrapper) {
            shortXContext.rule.addWebDavProfile(WebDavProfile.parseFrom(profile.byteData))
        }

        override fun removeWebDavProfile(id: String) {
            shortXContext.rule.removeWebDavProfile(id)
        }

        override fun getAllWebDavProfiles(): List<ByteArrayWrapper> {
            enforceCallingPermissions()
            return shortXContext.rule.getAllWebDavProfiles().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getWebDavProfileById(id: String): ByteArrayWrapper? {
            return shortXContext.rule.getAllWebDavProfiles().firstOrNull {
                it.profileId == id
            }?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getAllSystemEnv(): List<ByteArrayWrapper> {
            return SystemEnvInit.getAllEnv().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun setCustomSU(path: String?) {
            shortXContext.shell.setCustomSU(path)
        }

        override fun getCustomSU(): String? {
            return shortXContext.shell.getCustomSU()
        }

        override fun registerLightBarTransitionsListener(callback: ICallback) {
            shortXContext.statusbar.registerLightBarTransitionsListener(callback)
        }

        override fun unregisterLightBarTransitionsListener(callback: ICallback) {
            shortXContext.statusbar.unregisterLightBarTransitionsListener(callback)
        }

        override fun setLightBarTransitionsIconsDark(isDark: Boolean) {
            shortXContext.statusbar.setLightBarTransitionsIconsDark(isDark)
        }

        override fun isLightBarTransitionsIconsDark(): Boolean {
            return shortXContext.statusbar.isLightBarTransitionsIconsDark()
        }

        override fun setOCREngine(engine: Int) {
            shortXContext.ocr.setOCREngine(engine)
        }

        override fun getOCREngine(): Int {
            return shortXContext.ocr.getOCREngine().number
        }

        override fun attachSystemUIClient(token: SystemUIClient) {
            systemUIClient = token
            logger.w("attachSystemUIClient: $token")

            with(token) {
                shortXContext.shell.attachSUImpl("SystemUI", su)
                shortXContext.notification.attachNotificationPoster("SystemUI", poster)
                shortXContext.cv.attachCV(cv)
                shortXContext.ocr.attachOCR(ocr)
                shortXContext.ui.attachQSTileHost(tile)
            }
        }

        override fun getAllContextMenuSettings(): List<ByteArrayWrapper> {
            return shortXContext.systemSettings.getAllContextMenuSettings().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getContextMenuSettingsByMenuNumber(number: Int): ByteArrayWrapper? {
            return shortXContext.systemSettings.getContextMenuSettingsByMenuNumber(number)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun updateContextMenuSetting(setting: ByteArrayWrapper) {
            shortXContext.systemSettings.updateContextMenuSetting(
                CustomContextMenuSetting.parseFrom(
                    setting.byteData
                )
            )
        }

        override fun onMenuActionTrigger(number: Int, text: String) {
            shortXContext.systemSettings.onMenuActionTrigger(number, text)
        }

        override fun registerGestureSettingsListener(listener: ICallback) {
            shortXContext.gesture.registerGestureSettingsListener(listener)
        }

        override fun unregisterGestureSettingsListener(listener: ICallback) {
            shortXContext.gesture.unregisterGestureSettingsListener(listener)
        }

        override fun shouldFireEdgeGesture(edge: Int, gesture: Int): Boolean {
            val edgeE = Edge.forNumber(edge) ?: return false
            val gestureE = Gesture.forNumber(gesture) ?: return false
            val edgeGesture = EdgeGesture.newBuilder()
                .setEdge(edgeE)
                .setGesture(gestureE)
                .build()
            return shortXContext.rule.shouldFire(edgeGesture)
        }

        override fun fireEdgeGesture(edge: Int, gesture: Int) {
            logger.w("fireEdgeGesture - $edge $gesture")
            val edgeE = Edge.forNumber(edge) ?: return
            val gestureE = Gesture.forNumber(gesture) ?: return
            val edgeGesture = EdgeGesture.newBuilder()
                .setEdge(edgeE)
                .setGesture(gestureE)
                .build()
            shortXContext.rule.publishFact("fireEdgeGesture", edgeGesture) { emptyMap() }
        }

        override fun shouldFireScreenGesture(gesture: Int, x: Int, y: Int): Boolean {
            val gestureE = Gesture.forNumber(gesture) ?: return false
            return shortXContext.rule.shouldFire(
                ScreenGesture.newBuilder().setGesture(gestureE).build()
            )
        }

        override fun fireScreenGesture(gesture: Int, x: Int, y: Int) {
            val gestureE = Gesture.forNumber(gesture) ?: return
            shortXContext.rule.publishFact(
                "fireScreenGesture",
                ScreenGesture.newBuilder().setGesture(gestureE).build()
            ) {
                mapOf(
                    ContextDataMapping.ScreenGesture.eventX.name to x,
                    ContextDataMapping.ScreenGesture.eventY.name to y,
                )
            }
        }

        override fun registerConfigurationChangedListener(listener: ICallback) {
            shortXContext.systemSettings.registerConfigurationChangedListener(listener)
        }

        override fun unregisterConfigurationChangedListener(listener: ICallback) {
            shortXContext.systemSettings.unregisterConfigurationChangedListener(listener)
        }

        override fun getScreenSize(): Point {
            return shortXContext.wm.screenSize
        }

        override fun registerRecorderStateListener(listener: ICallback) {
            shortXContext.gesture.registerRecorderStateListener(listener)
        }

        override fun unregisterRecorderStateListener(listener: ICallback) {
            shortXContext.gesture.unregisterRecorderStateListener(listener)
        }

        override fun onMotionEvent(event: MotionEvent) {
            shortXContext.gesture.reportMotionEvent(event)
        }

        override fun isAppFeatureEnabled(feat: Int): Boolean {
            if (!isStarted) {
                logger.e("Access isAppFeatureEnabled - $feat, while system not started.")
                return false
            }
            if (!isSystemReady) {
                logger.w("Access isAppFeatureEnabled - $feat, while system not ready.")
            }
            return store.read(Settings.BoolSettings.EnableFeature(feat.toString()))
        }

        override fun setAppFeatureEnabled(feat: Int, enabled: Boolean) {
            store.write(Settings.BoolSettings.EnableFeature(feat.toString()), enabled)
            systemServiceScope.launchSafety("H.1") {
                if (feat == AppFeature.AppFeature_EdgeGesture.number) {
                    shortXContext.gesture.notifyGestureSettingsChanged()
                }
            }
        }

        override fun registerSystemReadyListener(callback: ICallback) {
            shortXContext.serviceLifecycle.registerLifecycleSystemReadyCallback(callback)
        }

        override fun unRegisterSystemReadyListener(callback: ICallback) {
            shortXContext.serviceLifecycle.unregisterLifecycleSystemReadyCallback(callback)
        }

        override fun registerFrontAppChangeListener(callback: ICallback) {
            shortXContext.appState.registerFrontAppChangeListener(callback)
        }

        override fun unRegisterFrontAppChangeListener(callback: ICallback) {
            shortXContext.appState.unRegisterFrontAppChangeListener(callback)
        }

        override fun getShortXServerDirs(): ByteArrayWrapper {
            return ByteArrayWrapper(
                ShortXServerDirs.newBuilder().setBaseServerDir(Dirs.baseServerDir.absolutePath)
                    .setImageCacheDir(Dirs.picassoCacheDir().absolutePath).build().toByteArray()
            )
        }

        override fun getAllDynamicShortcutSettings(): List<ByteArrayWrapper> {
            return shortXContext.appState.getAllDynamicShortcutSettings().map {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun getDynamicShortcutSettingsByNumber(number: Int): ByteArrayWrapper? {
            return shortXContext.appState.getDynamicShortcutSettingsByNumber(number)?.let {
                ByteArrayWrapper(it.toByteArray())
            }
        }

        override fun updateDynamicShortcutSetting(setting: ByteArrayWrapper) {
            val dynamicShortcutSetting = DynamicShortcutSetting.parseFrom(setting.byteData)
            shortXContext.appState.updateDynamicShortcutSetting(dynamicShortcutSetting)
        }

        override fun addToggle(toggle: ByteArrayWrapper) {
            enforceCallingPermissions()
            v(context) {
                shortXContext.rule.addToggle(Toggle.parseFrom(toggle.byteData))
            }
        }

        override fun setToggleEnabled(id: String, enable: Boolean) {
            shortXContext.rule.setToggleEnabled(id, enable)
        }

        override fun isToggleEnabled(id: String): Boolean {
            return shortXContext.rule.isToggleEnabled(id)
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

        override fun hasHookFlags(flag: String): Boolean {
            return File(Dirs.baseServerFlagsDir(), flag).exists()
        }

        override fun addHookFlags(flag: String) {
            File(Dirs.baseServerFlagsDir(), flag).apply {
                parentFile?.mkdirs()
                createNewFile()
            }
        }

        override fun removeHookFlags(flag: String) {
            File(Dirs.baseServerFlagsDir(), flag).deleteRecursively().also {
                logger.i("removeHookFlags: $it")
            }
        }

        override fun updateQSTileActiveState(number: Int) {
            shortXContext.statusbar.updateQSTileActiveState(number)
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
            handler.postSafety("reboot") {
                context.powerManager.reboot(null)
            }
        }
    }
}