package tornaco.apps.shortx.services

import android.annotation.CallSuper
import android.app.ActivityThread
import android.content.Context
import android.os.Binder
import android.os.Process
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.data.SettingsStore
import util.Reflections

abstract class SystemService(val shortXContext: ShortXContext, val serviceName: String) {
    private val logTag: String = serviceName

    private val bagOfTags: MutableMap<String, Any> = mutableMapOf()
    val handler by lazy {
        newHandlerOfNewThread(this.javaClass.simpleName)
    }

    val logger by lazy {
        Logger(logTag)
    }

    var isStarted: Boolean = false
        private set

    var isSystemReady: Boolean = false
        private set

    lateinit var context: Context
        private set

    lateinit var uiContext: Context
        private set

    val store: SettingsStore by lazy {
        require(isStarted) {
            "Access to Store before isStarted!"
        }
        SettingsStore(context, serviceName, systemServiceScope)
    }

    @CallSuper
    open fun start(context: Context) {
        this.context = context
        //
        // "android.display" prio=5 tid=24 Blocked
        //  | group="main" sCount=1 ucsCount=0 flags=1 obj=0x26eaee8 self=0xb400006f16a3b800
        //  | sysTid=3640 nice=-3 cgrp=ssfg sched=0/0 handle=0x6efee904c0
        //  | state=S schedstat=( 3324375 1601929 28 ) utm=0 stm=0 core=6 HZ=100
        //  | stack=0x6efea8d000-0x6efea8f000 stackSize=4109KB
        //  | held mutexes=
        //  at android.app.ActivityThread.getSystemUiContext(ActivityThread.java:3558)
        //  - waiting to lock <0x03395b92> (a android.app.ActivityThread) held by thread 64
        //  at android.app.ActivityThread.getSystemUiContext(ActivityThread.java:3548)
        //  at com.android.server.display.LocalDisplayAdapter.getOverlayContext(LocalDisplayAdapter.java:1820)
        //  at com.android.server.display.LocalDisplayAdapter$LocalDisplayDevice.loadDisplayDeviceConfig(LocalDisplayAdapter.java:647)
        //  at com.android.server.display.LocalDisplayAdapter$LocalDisplayDevice.getDisplayDeviceConfig(LocalDisplayAdapter.java:607)
        //  at com.android.server.display.LocalDisplayDeviceExtImpl.init(LocalDisplayDeviceExtImpl.java:109)
        //  at com.android.server.display.LocalDisplayAdapter$LocalDisplayDevice.<init>(LocalDisplayAdapter.java:371)
        //  at com.android.server.display.LocalDisplayAdapter.tryConnectDisplayLocked(LocalDisplayAdapter.java:262)
        //  at com.android.server.display.LocalDisplayAdapter.registerLocked(LocalDisplayAdapter.java:201)
        //  at com.android.server.display.DisplayManagerService.registerDisplayAdapterLocked(DisplayManagerService.java:2611)
        //  at com.android.server.display.DisplayManagerService.registerDefaultDisplayAdapters(DisplayManagerService.java:2545)
        //  - locked <0x03f2f506> (a com.android.server.display.DisplayManagerService$SyncRoot)
        //  at com.android.server.display.DisplayManagerService.-$$Nest$mregisterDefaultDisplayAdapters(unavailable:0)
        //  at com.android.server.display.DisplayManagerService$DisplayManagerHandler.handleMessage(DisplayManagerService.java:4506)
        //  at android.os.Handler.dispatchMessage(Handler.java:115)
        //  at android.os.Looper.loopOnce(Looper.java:298)
        //  at android.os.Looper.loop(Looper.java:408)
        //  at android.os.HandlerThread.run(HandlerThread.java:85)
        //  at com.android.server.ServiceThread.run(ServiceThread.java:49)
        //DumpLatencyMs: 15.6724
        //
        //"pool-3-thread-1" prio=5 tid=64 Blocked
        //  | group="main" sCount=1 ucsCount=0 flags=1 obj=0x2fc03d8 self=0xb400006f16b70c00
        //  | sysTid=3884 nice=0 cgrp=ssfg sched=0/0 handle=0x6ecbdfb4c0
        //  | state=S schedstat=( 54912343 8551512 45 ) utm=4 stm=1 core=6 HZ=100
        //  | stack=0x6ecb9f8000-0x6ecb9fa000 stackSize=4109KB
        //  | held mutexes=
        //  at com.android.server.display.DisplayManagerService.getDisplayInfoInternal(DisplayManagerService.java:1674)
        //  - waiting to lock <0x03f2f506> (a com.android.server.display.DisplayManagerService$SyncRoot) held by thread 24
        //  at com.android.server.display.DisplayManagerService.-$$Nest$mgetDisplayInfoInternal(unavailable:0)
        //  at com.android.server.display.DisplayManagerService$BinderService.getDisplayInfo(DisplayManagerService.java:5148)
        //  at android.hardware.display.DisplayManagerGlobal$1.recompute(DisplayManagerGlobal.java:223)
        //  at android.hardware.display.DisplayManagerGlobal$1.recompute(DisplayManagerGlobal.java:218)
        //  at android.app.PropertyInvalidatedCache.query(PropertyInvalidatedCache.java:1785)
        //  at android.hardware.display.DisplayManagerGlobal.getDisplayInfoLocked(DisplayManagerGlobal.java:274)
        //  at android.hardware.display.DisplayManagerGlobal.getDisplayInfo(DisplayManagerGlobal.java:263)
        //  - locked <@addr=0x3135098> (a java.lang.Object)
        //  at android.hardware.display.DisplayManagerGlobal.getCompatibleDisplay(DisplayManagerGlobal.java:375)
        //  at android.app.ResourcesManager.getAdjustedDisplay(ResourcesManager.java:587)
        //  at android.app.ContextImpl.getDisplayNoVerify(ContextImpl.java:3278)
        //  at android.app.ContextImpl.getDisplayId(ContextImpl.java:3287)
        //  at android.content.ContextWrapper.getDisplayId(ContextWrapper.java:1258)
        //  at android.app.ActivityThread.getSystemUiContextNoCreateLocked(ActivityThread.java:3603)
        //  at android.app.ActivityThread.getSystemUiContext(ActivityThread.java:3565)
        //  - locked <0x03395b92> (a android.app.ActivityThread)
        //  at android.app.ActivityThread.getSystemUiContext(ActivityThread.java:3548)
        //  at java.lang.reflect.Method.invoke(Native method)
        //  at hhc.hmjxgca.cnnbmz.Yo1.OooO00o(unavailable:30)
        //  at hhc.hmjxgca.cnnbmz.y62.OooO0o0(unavailable:9)
        //  at hhc.hmjxgca.cnnbmz.y62.OooO0oo(unavailable:7)
        //  at hhc.hmjxgca.cnnbmz.o00Oo0.run(unavailable:223)
        //  at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)
        //  at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)
        //  at java.lang.Thread.run(Thread.java:1119)
        //DumpLatencyMs: 25.5729
        // 移动到SystemReady。
        this.uiContext = context

        isStarted = true
        logger.i("start: $serviceName with context: $context")
    }

    @CallSuper
    open fun systemReady() {
        store.readBlocking()

        this.uiContext = getSystemUiContextOrNull() ?: context

        isSystemReady = true
        logger.i("systemReady: $this, uiContext: $uiContext")
    }

    open fun shutdown() {
        systemServiceScope.cancel("System shutdown.")
    }

    open fun screenOff() {

    }

    open fun screenOn() {

    }

    open fun userPresent(isFirstTime: Boolean) {

    }

    internal fun <T> getTag(key: String): T? {
        synchronized(bagOfTags) {
            @Suppress("UNCHECKED_CAST") return bagOfTags[key] as T
        }
    }

    internal fun <T> addTagIfAbsent(key: String, value: T): T {
        synchronized(bagOfTags) {
            if (bagOfTags.containsKey(key).not()) {
                bagOfTags[key] = requireNotNull(value) { "value should not be null for key: $key" }
            }
        }
        return value
    }

    private fun getSystemUiContextOrNull(): Context? {
        return kotlin.runCatching {
            val systemThread = ActivityThread.currentActivityThread()
            // systemThread.getSystemUiContext()
            Reflections.callMethod(systemThread, "getSystemUiContext") as Context?
        }.getOrElse {
            logger.e(it, "getSystemUiContextOrNull")
            null
        }
    }
}

fun <T> runClearCallingIdentity(action: () -> T?): T? {
    val id = Binder.clearCallingIdentity()
    return runCatching {
        action()
    }.fold(
        onSuccess = {
            Binder.restoreCallingIdentity(id)
            it
        }, onFailure = {
            Logger().e(it, "runClearCallingIdentity")
            Binder.restoreCallingIdentity(id)
            null
        })
}

val SystemService.systemServiceScope: CoroutineScope
    get() {
        require(isStarted) {
            "Trying to access systemServiceScope[- $serviceName] before System started!"
        }
        val key = "systemServiceScope"
        val scope: CoroutineScope? = this.getTag(key)
        if (scope != null) {
            return scope
        }
        return addTagIfAbsent(
            key,
            CoroutineScope(SupervisorJob() + handler.asCoroutineDispatcher("HD#Scope"))
        )
    }


fun SystemService.enforceCallingPermissions() {
    // Be careful
    if (Process.myPid() == Binder.getCallingPid()) {
        return
    }
    val callingUid = Binder.getCallingUid()
    if (PkgUtils.isSystemOrPhoneOrShell(callingUid)) {
        return
    }
    if (shortXContext.appState.isShortXAppUid(callingUid)) {
        logger.logOnDebug("enforceCallingPermissions, allow shortx of UID: $callingUid")
        return
    }
    if (shortXContext.appState.isSystemUIAppUid(callingUid)) {
        logger.logOnDebug("enforceCallingPermissions, allow SystemUI of UID: $callingUid")
        return
    }
    if (shortXContext.appState.isShortXStubAppUid(callingUid)) {
        logger.logOnDebug("enforceCallingPermissions, allow shortx stub of UID: $callingUid")
        return
    }
    throw SecurityException("Uid of $callingUid it not allowed to interact with ShortX server!!!")
}