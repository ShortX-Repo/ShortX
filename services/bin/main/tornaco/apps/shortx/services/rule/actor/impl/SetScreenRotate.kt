package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import android.os.ServiceManager
import android.view.IWindowManager
import android.view.Surface
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services34.wm.WindowManagerV34
import tornaco.apps.shortx.services35.wm.WindowManagerV35

object SetScreenRotate : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetScreenRotate::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SetScreenRotate::class.java
        val degree: ScreenRotateDegree = d.degree
        logger.d("degree" + degree.name)
        val wm = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE))
        if (degree == ScreenRotateDegree.ScreenRotateDegree_Any) {
            if (OsUtils.isVOrAbove()) {
                with(WindowManagerV35) {
                    wm.doThawRotation()
                }
            } else {
                with(WindowManagerV34) {
                    wm.doThawRotation()
                }
            }
        } else {
            // http://aospxref.com/android-8.0.0_r36/xref/frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java
            // freezeRotation(int rotation)

            // Android 15
            // https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/view/IWindowManager.aidl;l=339?q=thawRotation&start=11
            // freezeRotation(int rotation, String caller);

            if (OsUtils.isVOrAbove()) {
                with(WindowManagerV35) {
                    wm.doFreezeRotation(degree.degreeToRotation())
                }
            } else {
                with(WindowManagerV34) {
                    wm.doFreezeRotation(degree.degreeToRotation())
                }
            }
        }
        Actor.Res.EMPTY
    }
}

fun ScreenRotateDegree.degreeToRotation(): Int {
    return when (this) {
        ScreenRotateDegree.ScreenRotateDegree_0 -> {
            Surface.ROTATION_0
        }

        ScreenRotateDegree.ScreenRotateDegree_90 -> {
            Surface.ROTATION_90
        }

        ScreenRotateDegree.ScreenRotateDegree_180 -> {
            Surface.ROTATION_180
        }

        ScreenRotateDegree.ScreenRotateDegree_270 -> {
            Surface.ROTATION_270
        }

        else -> {
            Surface.ROTATION_0
        }
    }
}