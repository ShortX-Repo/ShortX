package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import tornaco.apps.shortx.ui.statusbar.StatusBarClipView
import util.Reflections

fun SystemUIHook.hookView(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
    fun View.getIdString(): String {
        val out = StringBuilder()
        val id = id
        if (id != View.NO_ID) {
            val r: Resources? = context.resources
            if (id > 0 && Resources.resourceHasPackage(id) && r != null) {
                try {
                    val pkgname = when (id and -0x1000000) {
                        0x7f000000 -> "app"
                        0x01000000 -> "android"
                        else -> r.getResourcePackageName(id)
                    }
                    val typename = r.getResourceTypeName(id)
                    val entryname = r.getResourceEntryName(id)
                    out.append(pkgname)
                    out.append(":")
                    out.append(typename)
                    out.append("/")
                    out.append(entryname)
                } catch (e: Resources.NotFoundException) {
                }
            }
        }
        return out.toString()
    }

    fun ViewGroup.dumpChild(prefix: String = "") {
        repeat(childCount) {
            val child = getChildAt(it)
            logger.d("$prefix child: $child")
            if (child is ViewGroup) {
                child.dumpChild(prefix + "\t")
            }
        }
    }

    if (PackageNames.systemUI == lpparam.packageName) {
        afterMethod(
            clazz = Reflections.findClass(
                "com.android.systemui.statusbar.phone.PhoneStatusBarView",
                lpparam.classLoader
            ),
            methodName = "onFinishInflate"
        ) { param ->
            logger.d("onFinishInflate: ${param.args.map { it }}")
            val view: ViewGroup = param.thisObject as ViewGroup
            view.dumpChild()
            repeat(view.childCount) { index ->
                val child = view.getChildAt(index)
                val id = child.getIdString()
                if (id == "app:id/status_bar_contents") {
                    logger.d("status_bar_contents: $child")
                    kotlin.runCatching {
                        val linearRoot = child as LinearLayout
                        linearRoot.addView(StatusBarClipView(child.context), 0)
                        logger.d("Clip added.")
                    }.onFailure {
                        logger.e(it, "Fail to add clip")
                    }
                }
            }
        }
    }
}.onFailure {
    logger.e(it, "hookView")
}