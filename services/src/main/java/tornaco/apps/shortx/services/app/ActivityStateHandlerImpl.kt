package tornaco.apps.shortx.services.app

import android.content.ComponentName
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.appComponentToString
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.toAppComponent
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.android.ServiceRetriever

class ActivityStateHandlerImpl(val context: Context, val shortXContext: ShortXContext) {
    private val logger = Logger("ActivityStateHandlerImpl")

    private val _currentResumedActivities: MutableStateFlow<List<AppComponent>> = MutableStateFlow(
        emptyList()
    )

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

    fun isFront(appPkg: AppPkg): Boolean {
        val topVisibleActivities = func.getTopVisibleActivities().also { appComponents ->
            logger.d("Check isFront, topVisibleActivities: ${appComponents.map { it.appComponentToString() }}")
        }
        return topVisibleActivities.any { it.pkg == appPkg }
    }

    fun onActivityResumed(componentName: ComponentName, userId: Int, intentUri: String) {
        logger.d("onActivityResumed: $componentName userId: $userId, intentUri: $intentUri")

        onActivityStateChanged()

        val activity = componentName.toAppComponent(userId)
        shortXContext.rule.publishFact(
            reason = "onActivityResumed",
            fact = ActivityStarted.newBuilder()
                .addComponents(activity)
                .build(),
            initialContextData = mapOf(
                ContextDataMapping.ActivityStarted.activityIntentUri.name to intentUri,
                ContextDataMapping.ActivityStarted.pkgName.name to activity.pkg.pkgName,
                ContextDataMapping.ActivityStarted.componentName.name to componentName.flattenToShortString(),
                ContextDataMapping.ActivityStarted.appLabel.name to (shortXContext.appState.getAppLabel(
                    activity.pkg
                ) ?: activity.pkg.pkgName),
            )
        )

    }

    fun onActivityStopped(componentName: ComponentName, userId: Int) {
        // Just dump.
        logger.d("onActivityStopped: $componentName userId: $userId")
        onActivityStateChanged()
    }

    private fun onActivityStateChanged() {
        val topVisibleActivities = func.getTopVisibleActivities()
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
            shortXContext.rule.publishFact(
                reason = "onActivityResumed",
                fact = AppBecomeFg.newBuilder().addApps(it).build(),
                initialContextData = mapOf(
                    ContextDataMapping.AppBecomeFg.pkgName.name to it.pkgName,
                    ContextDataMapping.AppBecomeFg.appLabel.name to (shortXContext.appState.getAppLabel(
                        it
                    ) ?: it.pkgName),
                )
            )
        }

        removedApps.forEach {
            logger.i("---------> Pkg to BACKGROUND: $it")
            shortXContext.rule.publishFact(
                reason = "onActivityResumed",
                fact = AppBecomeBg.newBuilder().addApps(it).build(),
                initialContextData = mapOf(
                    ContextDataMapping.AppBecomeBg.pkgName.name to it.pkgName,
                    ContextDataMapping.AppBecomeBg.appLabel.name to (shortXContext.appState.getAppLabel(
                        it
                    )
                        ?: it.pkgName),
                )
            )
        }
        logger.i("----> Update _currentResumedActivities to: ${topVisibleActivities.map { it.appComponentToString() }}")
        _currentResumedActivities.value = topVisibleActivities
    }
}

private interface Funs {
    fun getTopVisibleActivities(): List<AppComponent>
}

// http://aospxref.com/android-10.0.0_r47/xref/frameworks/base/services/core/java/com/android/server/wm/ActivityTaskManagerService.java#6125
private class FuncV29(val context: Context) : Funs {
    private val logger = Logger("FuncV29")
    private val atmsInt by lazy {
        ServiceRetriever(context.classLoader).getATMSInternal().also { logger.w("atmsInt=$it") }
    }

    override fun getTopVisibleActivities(): List<AppComponent> {
        return ATMSFuncs.getTopVisibleActivities(requireNotNull(atmsInt) { "atmsInt is null." })
    }
}

// http://aospxref.com/android-9.0.0_r61/search?project=frameworks&full=getTopVisibleActivities&defs=&refs=&path=&hist=&type=&xrd=&nn=1
private class FuncV28(val context: Context) : Funs {
    private val logger = Logger("FuncV28")
    private val amsInt by lazy {
        ServiceRetriever(context.classLoader).getAMSInternal().also { logger.w("amsInt=$it") }
    }

    override fun getTopVisibleActivities(): List<AppComponent> {
        return AMSIntFuncs.getTopVisibleActivities(requireNotNull(amsInt) { "amsInt is null." })
    }
}