package tornaco.apps.shortx.services.statusbar

import android.annotation.SuppressLint
import android.content.Context
import android.view.WindowInsets
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.android.ServiceHolder
import util.Reflections

object StatusBarManagerServiceFuncs {
    private val logger = Logger("StatusBarManagerServiceFuncs")

    fun setIcon(
        slot: String,
        iconId: Int,
        iconPackage: String = BuildPropInternal.APP_PACKAGE_NAME,
        iconLevel: Int = 0,
        contentDescription: String = ""
    ) {
        val sbms = requireNotNull(ServiceHolder.statusBarManagerService) {
            "statusBarManagerService is missing."
        }
        Reflections.callMethod(
            sbms,
            "setIcon",
            slot,
            iconPackage,
            iconId,
            iconLevel,
            contentDescription
        )
    }

    fun removeIcon(
        slot: String,
    ) {
        val sbms = requireNotNull(ServiceHolder.statusBarManagerService) {
            "statusBarManagerService is missing."
        }
        Reflections.callMethod(
            sbms,
            "removeIcon",
            slot
        )
    }

    fun showRecentApps() {
        // void showRecentApps(boolean triggeredFromAltTab)
        val internal = getInternalService()
        Reflections.callMethod(
            internal,
            "showRecentApps",
            false
        )
    }

    fun hideRecentApps() {
        // void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHomeKey)
        val internal = getInternalService()
        Reflections.callMethod(
            internal,
            "hideRecentApps",
            false,
            false
        )
    }

    fun showGlobalActionsMenu() {
        // mBar
        // void showGlobalActionsMenu()
        val bar = getMBar()
        Reflections.callMethod(
            bar,
            "showGlobalActionsMenu"
        )
    }

    // Since Android 11
    @SuppressLint("NewApi")
    fun showTransient(context: Context) {
        //    /** @see com.android.internal.statusbar.IStatusBar#showTransient */
        //    void showTransient(int displayId, @InsetsType int types, boolean isGestureOnSystemBar);
        //
        //    /** @see com.android.internal.statusbar.IStatusBar#abortTransient */
        //    void abortTransient(int displayId, @InsetsType int types);
        val internal = getInternalService()
        Reflections.callMethod(
            internal,
            "showTransient",
            context.displayId,
            WindowInsets.Type.statusBars(),
            false
        )
    }

    // Since Android 11
    @SuppressLint("NewApi")
    fun hideOrShowInsets(@WindowInsets.Type.InsetsType types: Int, isHide: Boolean) {
        val displayPolicy = ServiceHolder.displayPolicy
        logger.d("displayPolicy: $displayPolicy")
        val focusedWindow = Reflections.getObjectField(displayPolicy, "mFocusedWindow")
        logger.d("focusedWindow: $focusedWindow")
        if (focusedWindow != null) {
            // Android 14+
            //   @Override
            //    public void hideInsets(@InsetsType int types, boolean fromIme,
            //            @Nullable ImeTracker.Token statsToken)
            val methodName = if (isHide) "hideInsets" else "showInsets"
            if (OsUtils.isUOrAbove()) {
                Reflections.callMethod(
                    focusedWindow,
                    methodName,
                    types,
                    false,
                    null
                )
            } else {
                Reflections.callMethod(
                    focusedWindow,
                    methodName,
                    types,
                    false,
                )
            }
        }
    }

    private fun getInternalService(): Any {
        val sbms = requireNotNull(ServiceHolder.statusBarManagerService) {
            "statusBarManagerService is missing."
        }
        // StatusBarManagerInternal mInternalService:
        val internal = requireNotNull(Reflections.getObjectField(sbms, "mInternalService")) {
            "Can not get mInternalService"
        }
        return internal
    }

    private fun getMBar(): Any {
        val sbms = requireNotNull(ServiceHolder.statusBarManagerService) {
            "statusBarManagerService is missing."
        }
        // IStatusBar mBar:
        val bar = requireNotNull(Reflections.getObjectField(sbms, "mBar")) {
            "Can not get mBar"
        }
        return bar
    }
}