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
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.android.ServiceRetriever

class ActivityStateHandlerImpl(val context: Context, val shortXContext: ShortXContext) {
    private val logger = Logger("ActivityStateHandlerImpl")

    private val _currentResumedActivities: MutableStateFlow<List<AppComponent>> = MutableStateFlow(
        emptyList()
    )

    private val func: Funs by lazy {
        if (OsUtils.isROrAbove()) {
            FuncV29(context)
        } else {
            FuncV28(context)
        }
    }

    fun isFront(appPkg: AppPkg): Boolean {
        val topVisibleActivities = func.getTopVisibleActivities().also {
            // Update our cache...
            _currentResumedActivities.value = it
        }
        return topVisibleActivities.any { it.pkg == appPkg }
    }

    fun onActivityResumed(componentName: ComponentName, userId: Int) {
        logger.d("onActivityResumed: $componentName userId: $userId")

        val topVisibleActivities = func.getTopVisibleActivities()
        logger.d("topVisibleActivities: ${topVisibleActivities.map { it.appComponentToString() }}")
        if (topVisibleActivities.isEmpty()) {
            logger.e("Can not get topVisibleActivities")
            return
        }

        val currentResumedActivities = _currentResumedActivities.value
        logger.d("currentResumedActivities: ${currentResumedActivities.map { it.appComponentToString() }}")

        val activity = componentName.toAppComponent(userId)
        val activityPkg = activity.pkg
        val isThisPkgFrontBefore = currentResumedActivities.any { it.pkg == activityPkg }

        if (!isThisPkgFrontBefore) {
            logger.i("This package changed from back to front: $activityPkg")
            shortXContext.rule.publishFact(
                reason = "onActivityResumed",
                fact = AppBecomeFg.newBuilder().addApps(activityPkg).build(),
                initialContextData = mapOf(
                    ContextDataMapping.AppBecomeFg.componentName.name to componentName.flattenToShortString(),
                    ContextDataMapping.AppBecomeFg.pkgName.name to activityPkg.pkgName,
                    ContextDataMapping.AppBecomeFg.appLabel.name to (shortXContext.appState.getAppLabel(
                        activityPkg
                    )
                        ?: activityPkg.pkgName),
                )
            )
        }


        logger.i("Publish Activity resume fact.")
        _currentResumedActivities.value = topVisibleActivities

        shortXContext.rule.publishFact(
            reason = "onComponentMoveToFg",
            fact = ActivityStarted.newBuilder()
                .addComponents(activity)
                .build(),
            initialContextData = mapOf(
                ContextDataMapping.ActivityStarted.pkgName.name to activityPkg.pkgName,
                ContextDataMapping.ActivityStarted.componentName.name to componentName.flattenToShortString(),
                ContextDataMapping.ActivityStarted.appLabel.name to (shortXContext.appState.getAppLabel(
                    activityPkg
                )
                    ?: activityPkg.pkgName),
            )
        )
    }

    fun onActivityStopped(componentName: ComponentName, userId: Int) {
        logger.d("onActivityStopped: $componentName userId: $userId")

        val topVisibleActivities = func.getTopVisibleActivities()
        logger.d("topVisibleActivities: ${topVisibleActivities.map { it.appComponentToString() }}")
        if (topVisibleActivities.isEmpty()) {
            logger.e("Can not get topVisibleActivities")
            return
        }

        val activity = componentName.toAppComponent(userId)
        val activityPkg = activity.pkg
        val isThisPkgFrontNow = topVisibleActivities.any { it.pkg == activityPkg }

        _currentResumedActivities.value = topVisibleActivities

        if (!isThisPkgFrontNow) {
            logger.i("This pkg has moved to background: $activityPkg")
            shortXContext.rule.publishFact(
                reason = "onActivityStopped",
                fact = AppBecomeBg.newBuilder().addApps(activityPkg).build(),
                initialContextData = mapOf(
                    ContextDataMapping.AppBecomeBg.pkgName.name to activityPkg.pkgName,
                    ContextDataMapping.AppBecomeBg.appLabel.name to (shortXContext.appState.getAppLabel(
                        activityPkg
                    )
                        ?: activityPkg.pkgName),
                )
            )
        }
    }

}

private interface Funs {
    fun getTopVisibleActivities(): List<AppComponent>
}

// http://aospxref.com/android-10.0.0_r47/xref/frameworks/base/services/core/java/com/android/server/wm/ActivityTaskManagerService.java#6125
private class FuncV29(val context: Context) : Funs {
    private val atmsInt by lazy {
        ServiceRetriever(context.classLoader).getATMSInternal()
    }

    override fun getTopVisibleActivities(): List<AppComponent> {
        return ATMSFuncs.getTopVisibleActivities(requireNotNull(atmsInt) { "atmsInt is null." })
    }
}

// http://aospxref.com/android-9.0.0_r61/search?project=frameworks&full=getTopVisibleActivities&defs=&refs=&path=&hist=&type=&xrd=&nn=1
private class FuncV28(val context: Context) : Funs {
    private val amsInt by lazy {
        ServiceRetriever(context.classLoader).getATMSInternal()
    }

    override fun getTopVisibleActivities(): List<AppComponent> {
        return emptyList()
    }
}