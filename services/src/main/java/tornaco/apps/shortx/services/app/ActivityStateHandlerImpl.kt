package tornaco.apps.shortx.services.app

import android.content.ComponentName
import android.content.Context
import android.os.RemoteCallbackList
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.appComponentToString
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AppPkgList
import tornaco.apps.shortx.core.proto.fact.ActivityDestroyed
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.ActivityStopped
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.toAppComponent
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services.rule.FactPublishData

class ActivityStateHandlerImpl(val context: Context, val shortXContext: ShortXContext) {
    private val logger = Logger("ActivityStateHandlerImpl")

    private val _currentResumedActivities: MutableStateFlow<List<AppComponent>> = MutableStateFlow(
        emptyList()
    )

    val currentResumedActivities
        get() = _currentResumedActivities.value

    val currentFrontAppPkgs
        get() = _currentResumedActivities.value.map {
            it.pkg
        }.distinct()

    private val func: Funs by lazy {
        if (OsUtils.isQOrAbove()) {
            // Android10
            FuncV29(context)
        } else {
            FuncV28(context)
        }
    }

    private val frontAppChangeListeners = RemoteCallbackList<ICallback>()

    fun isFront(appPkg: AppPkg): Boolean {
        val topVisibleActivities = func.getTopVisibleActivities()
            .filter {
                val isInDefaultDisplay = it.isInDefaultDisplay
                if (!isInDefaultDisplay) {
                    logger.w("Ignore isFront in display: ${it.displayId} ${it.content.appComponentToString()}")
                }
                isInDefaultDisplay
            }.map { it.content }
            .also { appComponents ->
                logger.d("Check isFront, topVisibleActivities: ${appComponents.map { it.appComponentToString() }}")
            }
        return topVisibleActivities.any { it.pkg == appPkg }
    }

    fun onActivityResumed(
        componentName: ComponentName,
        userId: Int,
        intentUri: String,
        taskLabel: String?
    ) {
        logger.d("onActivityResumed: $componentName userId: $userId, intentUri: $intentUri $taskLabel")

        onActivityStateChanged()

        val activity = componentName.toAppComponent(userId)
        shortXContext.rule.publishFactChecked(
            ActivityStarted.getDefaultInstance()
        ) {
            FactPublishData(
                reason = "onActivityResumed",
                fact = ActivityStarted.newBuilder()
                    .addComponents(activity)
                    .build()
            ) {
                mapOf(
                    ContextDataMapping.ActivityStarted.activityIntentUri.name to intentUri,
                    ContextDataMapping.ActivityStarted.pkgName.name to activity.pkg.pkgName,
                    ContextDataMapping.ActivityStarted.userId.name to activity.pkg.userId,
                    ContextDataMapping.ActivityStarted.taskLabel.name to taskLabel.orEmpty(),
                    ContextDataMapping.ActivityStarted.componentName.name to componentName.flattenToShortString(),
                    ContextDataMapping.ActivityStarted.appLabel.name to (shortXContext.appState.getAppLabel(
                        activity.pkg
                    ) ?: activity.pkg.pkgName),
                )
            }
        }
    }

    fun onActivityStopped(componentName: ComponentName, userId: Int, taskLabel: String?) {
        // Just dump.
        logger.d("onActivityStopped: $componentName userId: $userId $taskLabel")
        onActivityStateChanged()

        val activity = componentName.toAppComponent(userId)
        shortXContext.rule.publishFactChecked(
            ActivityStopped.getDefaultInstance()
        ) {
            FactPublishData(
                reason = "onActivityStopped",
                fact = ActivityStopped.newBuilder()
                    .addComponents(activity)
                    .build()
            ) {
                mapOf(
                    ContextDataMapping.ActivityStopped.pkgName.name to activity.pkg.pkgName,
                    ContextDataMapping.ActivityStopped.userId.name to activity.pkg.userId,
                    ContextDataMapping.ActivityStarted.taskLabel.name to taskLabel.orEmpty(),
                    ContextDataMapping.ActivityStopped.componentName.name to componentName.flattenToShortString(),
                    ContextDataMapping.ActivityStopped.appLabel.name to (shortXContext.appState.getAppLabel(
                        activity.pkg
                    ) ?: activity.pkg.pkgName),
                )
            }
        }
    }

    fun onActivityDestroyed(componentName: ComponentName, userId: Int, taskLabel: String?) {
        logger.d("onActivityDestroyed: $componentName userId: $userId $taskLabel")

        shortXContext.rule.publishFactChecked(ActivityDestroyed.getDefaultInstance()) {
            val activity = componentName.toAppComponent(userId)
            FactPublishData(
                reason = "onActivityDestroyed",
                fact = ActivityDestroyed.newBuilder()
                    .addComponents(activity)
                    .build()
            ) {
                mapOf(
                    ContextDataMapping.ActivityStopped.pkgName.name to activity.pkg.pkgName,
                    ContextDataMapping.ActivityStopped.userId.name to activity.pkg.userId,
                    ContextDataMapping.ActivityStopped.componentName.name to componentName.flattenToShortString(),
                    ContextDataMapping.ActivityStarted.taskLabel.name to taskLabel.orEmpty(),
                    ContextDataMapping.ActivityStopped.appLabel.name to (shortXContext.appState.getAppLabel(
                        activity.pkg
                    ) ?: activity.pkg.pkgName),
                )
            }
        }
    }

    private fun onActivityStateChanged() {
        // WTF? Reported on MIUI android33 sometimes.
        // topVisibleActivities: [tv.danmaku.bili-tv.danmaku.bili.MainActivityV2 u0,
        // com.miui.home-com.miui.home.launcher.Launcher u0,
        // com.rdstory.miuiperfsaver-com.rdstory.miuiperfsaver.ui.MainActivity u0,
        // bin.mt.plus-bin.mt.plus.MainLightIcon u0, tornaco.apps.shortx-tornaco.apps.shortx.ui.main.MainActivity u0,
        // com.sulisong.uperf-com.sulisong.uperf.MainActivity u0,
        // com.ss.android.ugc.aweme-com.ss.android.ugc.aweme.splash.SplashActivity u0,
        // com.tencent.mm-com.tencent.mm.ui.LauncherUI u0, com.coolapk.market-com.coolapk.market.view.main.MainActivity u0,
        // com.miui.securitycenter-com.miui.powercenter.PowerMainActivity u0, com.paget96.batteryguru-activities.MainActivity u0,
        // com.xiaomi.misettings-com.xiaomi.misettings.display.AntiFlickerMode.AntiFlickerActivity u0,
        // com.tencent.mobileqq-com.tencent.mobileqq.activity.SplashActivity u0,
        // com.miui.calculator-com.miui.calculator.cal.CalculatorActivity u0,
        // com.sevtinge.cemiuiler-com.sevtinge.cemiuiler.ui.HideAppActivity u0,
        // com.miui.home-com.miui.home.launcher.AddItemActivity u0,


        // OPLUS Preload feature
        // ActivityStateHandlerImpl [binder:2977_6] ---->
        // Update _currentResumedActivities to: [com.android.launcher-com.android.launcher.Launcher u0, me.zhanghai.android.files-me.zhanghai.android.files.filelist.FileListActivity u0]
        //  ActivityStateHandlerImpl [main] onActivityResumed: ComponentInfo{android/com.android.server.wm.OplusActivityPreloadFakeActivity} userId: 0,
        //  intentUri: intent:#Intent;launchFlags=0x10800000;component=android/com.android.server.wm.OplusActivityPreloadFakeActivity;end
        //  ActivityStateHandlerImpl [main] topVisibleActivities: [com.android.launcher-com.android.launcher.Launcher u0, android-com.android.server.wm.OplusActivityPreloadFakeActivity u0]
        //  ActivityStateHandlerImpl [main] topVisibleApps: [com.android.launcher-u0, android-u0]
        //  ActivityStateHandlerImpl [main] currentResumedActivities: [com.android.launcher-com.android.launcher.Launcher u0, me.zhanghai.android.files-me.zhanghai.android.files.filelist.FileListActivity u0]
        //  ActivityStateHandlerImpl [main] currentResumedApps: [com.android.launcher-u0, me.zhanghai.android.files-u0]
        //  ActivityStateHandlerImpl [main] addedApps: [android-u0]
        //  ActivityStateHandlerImpl [main] removedApps: [me.zhanghai.android.files-u0]
        //  ActivityStateHandlerImpl [main] +++++++++> Pkg to FRONT: pkgName: "android"
        //  ActivityStateHandlerImpl [main] ---------> Pkg to BACKGROUND: pkgName: "me.zhanghai.android.files"
        //  ActivityStateHandlerImpl [main] ----> Update _currentResumedActivities to: [com.android.launcher-com.android.launcher.Launcher u0, android-com.android.server.wm.OplusActivityPreloadFakeActivity u0]
        // public int startActivityInPreloadDisplay(int displayId) {
        //        int duration;
        //        if (this.mPreloadPendingIntent == null) {
        //            Slog.e(TAG, "startActivityInPreloadDisplay Intent null!");
        //            this.mHistoryLog.addKeyInfo("ERROR_UNEXPECTED_NOPRELOADING");
        //            return -5;
        //        }
        //        ActivityOptions options = ActivityOptions.makeBasic();
        //        options.setLaunchDisplayId(displayId);
        //        Slog.i(TAG, "startActivityInPreloadDisplay(" + displayId + "),intent: " + this.mPreloadPendingIntent);
        //        this.mContext.startActivity(this.mPreloadPendingIntent, options.toBundle());
        //        this.mHistoryLog.addKeyInfo("startActivity " + this.mPreloadPendingIntent.getComponent());
        //        if (this.mPreloadingDuration > 0) {
        //            duration = this.mPreloadingDuration;
        //        } else {
        //            duration = this.mIsGameActivity ? this.mGameAcPreloadDur : this.mAcPreloadDur;
        //        }
        //        Message msg = this.mAcPHandler.obtainMessage(101);
        //        this.mAcPHandler.sendMessageDelayed(msg, duration);
        //        return 0;
        //    }

        val topVisibleActivities =
            func.getTopVisibleActivities()
                .filter {
                    val isInDefaultDisplay = it.isInDefaultDisplay
                    if (!isInDefaultDisplay) {
                        logger.w("Ignore topVisibleActivity in display: ${it.displayId} ${it.content.appComponentToString()}")
                    }
                    isInDefaultDisplay
                }.map { it.content }
                .take(2)
        val topVisibleApps = topVisibleActivities.map { it.pkg }.distinct()
        logger.d("topVisibleActivities: ${topVisibleActivities.map { it.appComponentToString() }}")
        logger.d("topVisibleApps: ${topVisibleApps.map { it.appPkgToString() }}")

        val currentResumedActivities = _currentResumedActivities.value
        val currentResumedApps = currentResumedActivities.map { it.pkg }.distinct()
        logger.d("currentResumedActivities: ${currentResumedActivities.map { it.appComponentToString() }}")
        logger.d("currentResumedApps: ${currentResumedApps.map { it.appPkgToString() }}")

        // Added
        val addedApps = topVisibleApps.filterNot { currentResumedApps.contains(it) }
        val removedApps = currentResumedApps.filterNot { topVisibleApps.contains(it) }

        logger.d("addedApps: ${addedApps.map { it.appPkgToString() }}")
        logger.d("removedApps: ${removedApps.map { it.appPkgToString() }}")

        addedApps.forEach {
            logger.i("+++++++++> Pkg to FRONT: $it")
            shortXContext.rule.publishFactChecked(
                AppBecomeFg.getDefaultInstance()
            ) {
                FactPublishData(
                    reason = "onActivityResumed",
                    fact = AppBecomeFg.newBuilder().addApps(it).build(),
                    initialContextDataLazy = {
                        mapOf(
                            ContextDataMapping.AppBecomeFg.pkgName.name to it.pkgName,
                            ContextDataMapping.AppBecomeFg.userId.name to it.userId,
                            ContextDataMapping.AppBecomeFg.appLabel.name to (shortXContext.appState.getAppLabel(
                                it
                            ) ?: it.pkgName),
                        )
                    }
                )
            }
        }

        removedApps.forEach {
            logger.i("---------> Pkg to BACKGROUND: $it")
            shortXContext.rule.publishFactChecked(AppBecomeBg.getDefaultInstance()) {
                FactPublishData(
                    reason = "onActivityResumed",
                    fact = AppBecomeBg.newBuilder().addApps(it).build(),
                    initialContextDataLazy = {
                        mapOf(
                            ContextDataMapping.AppBecomeBg.pkgName.name to it.pkgName,
                            ContextDataMapping.AppBecomeBg.userId.name to it.userId,
                            ContextDataMapping.AppBecomeBg.appLabel.name to (shortXContext.appState.getAppLabel(
                                it
                            )
                                ?: it.pkgName),
                        )
                    }
                )
            }
        }
        logger.i("----> Update _currentResumedActivities to: ${topVisibleActivities.map { it.appComponentToString() }}")
        _currentResumedActivities.value = topVisibleActivities

        // Front changed.
        if (addedApps.isNotEmpty() || removedApps.isNotEmpty()) {
            broadcastFrontAppChanged()
        }
    }

    fun registerFrontAppChangeListener(callback: ICallback) {
        synchronized(frontAppChangeListeners) {
            frontAppChangeListeners.register(callback)
        }
    }

    fun unRegisterFrontAppChangeListener(callback: ICallback) {
        synchronized(frontAppChangeListeners) {
            frontAppChangeListeners.unregister(callback)
        }
    }

    private fun broadcastFrontAppChanged() {
        val appPkgList = AppPkgList.newBuilder()
            .addAllAppPkg(currentFrontAppPkgs)
            .build()
        logger.logOnDebug {
            "broadcastFrontAppChanged: $appPkgList"
        }
        synchronized(frontAppChangeListeners) {
            frontAppChangeListeners.broadcast {
                runCatching {
                    it.receive(ByteArrayWrapper(appPkgList.toByteArray()))
                }.onFailure {
                    logger.e(it, "broadcastFrontAppChanged")
                }
            }
        }
    }
}

private interface Funs {
    fun getTopVisibleActivities(): List<InDisplay<AppComponent>>
}

// http://aospxref.com/android-10.0.0_r47/xref/frameworks/base/services/core/java/com/android/server/wm/ActivityTaskManagerService.java#6125
private class FuncV29(val context: Context) : Funs {
    private val logger = Logger("FuncV29")
    private val atmsInt by lazy {
        ServiceRetriever(context.classLoader).getATMSInternal().also { logger.w("atmsInt=$it") }
    }

    override fun getTopVisibleActivities(): List<InDisplay<AppComponent>> {
        return ATMSFuncs.getTopVisibleActivities(requireNotNull(atmsInt) { "atmsInt is null." })
    }
}

// http://aospxref.com/android-9.0.0_r61/search?project=frameworks&full=getTopVisibleActivities&defs=&refs=&path=&hist=&type=&xrd=&nn=1
private class FuncV28(val context: Context) : Funs {
    private val logger = Logger("FuncV28")
    private val amsInt by lazy {
        ServiceRetriever(context.classLoader).getAMSInternal().also { logger.w("amsInt=$it") }
    }

    override fun getTopVisibleActivities(): List<InDisplay<AppComponent>> {
        return AMSIntFuncs.getTopVisibleActivities(requireNotNull(amsInt) { "amsInt is null." })
    }
}