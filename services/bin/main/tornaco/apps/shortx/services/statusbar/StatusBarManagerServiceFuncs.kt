package tornaco.apps.shortx.services.statusbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.UserHandle
import android.util.ArrayMap
import android.view.WindowInsets
import com.android.internal.statusbar.IStatusBar
import com.android.internal.statusbar.StatusBarIcon
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util._dp
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

    @Suppress("UNCHECKED_CAST")
    fun setIcon2(
        res: AppResources,
        slot: String,
        iconId: Int,
        iconPackage: String = BuildPropInternal.APP_PACKAGE_NAME,
        iconLevel: Int = 0,
        contentDescription: String = ""
    ) {
        val sbms = requireNotNull(ServiceHolder.statusBarManagerService) {
            "statusBarManagerService is missing."
        }

        fun resizeIcon(targetWidth: Int): Icon {
            val drawable: Drawable =
                requireNotNull(res.appContext.getDrawable(iconId)) { "Drawable is null" }
            val bitmap = BitmapUtils.createScaledBitmap(
                BitmapUtils.getBitmap(res.appContext, drawable),
                targetWidth,
                targetWidth
            )
            return Icon.createWithBitmap(bitmap)
        }

        val iconScaled = resizeIcon(16._dp)
        logger.d("setIcon2 iconScaled: $iconScaled")


        //  private final ArrayMap<String, StatusBarIcon> mIcons = new ArrayMap<>();
        //  synchronized (mIcons) {
        //            StatusBarIcon icon = new StatusBarIcon(iconPackage, UserHandle.SYSTEM, iconId,
        //                    iconLevel, 0, contentDescription, StatusBarIcon.Type.SystemIcon);
        //            //Slog.d(TAG, "setIcon slot=" + slot + " index=" + index + " icon=" + icon);
        //            mIcons.put(slot, icon);
        //
        //            IStatusBar bar = mBar;
        //            if (bar != null) {
        //                try {
        //                    bar.setIcon(slot, icon);
        //                } catch (RemoteException ex) {
        //                }
        //            }
        //        }


        val mIcons: ArrayMap<String, StatusBarIcon> =
            Reflections.getObjectField(sbms, "mIcons") as ArrayMap<String, StatusBarIcon>
        synchronized(mIcons) {
            logger.d("setIcon2 mIcons: $mIcons")
            val icon = if (OsUtils.isVOrAbove()) {
                tornaco.apps.shortx.services35.systemui.StatusBarIconCreator.createIcon(
                    UserHandle.SYSTEM,
                    iconPackage,
                    iconScaled,
                    iconLevel,
                    0,
                    contentDescription,
                )
            } else {
                tornaco.apps.shortx.services34.systemui.StatusBarIconCreator.createIcon(
                    UserHandle.SYSTEM,
                    iconPackage,
                    iconScaled,
                    iconLevel,
                    0,
                    contentDescription,
                )
            }
            logger.d("setIcon2 icon: $icon")

            mIcons[slot] = icon
            val bar = Reflections.getObjectField(sbms, "mBar") as IStatusBar
            bar.setIcon(slot, icon)
        }
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