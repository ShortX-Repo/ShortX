package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.services.xposed.hooks.hook.afterConstruct
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import tornaco.apps.shortx.ui.statusbar.StatusBarClipView
import util.Reflections

fun SystemUIHook.hookView(lpparam: IPackageLoaded.Param) = kotlin.runCatching {
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

    fun ViewGroup.findChild(targetId: String): View? {
        repeat(childCount) {
            val child = getChildAt(it)
            val id = child.getIdString()
            if (id == targetId) {
                return child
            }
            if (child is ViewGroup) {
                val found = child.findChild(targetId)
                if (found != null) {
                    return found
                }
            }
        }
        return null
    }

    if (PackageNames.systemUI == lpparam.packageName) {
        // MiuiPhoneStatusBarView
        val clazz = runCatching {
            Reflections.findClass(
                "com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView",
                lpparam.classLoader
            )
        }.getOrElse {
            Reflections.findClass(
                "com.android.systemui.statusbar.phone.PhoneStatusBarView",
                lpparam.classLoader
            )
        }
        logger.w("PhoneStatusBarView class: $clazz")
        afterMethod(
            clazz = clazz,
            methodName = "onFinishInflate"
        ) { param ->
            logger.d("onFinishInflate: ${param.args.map { it }}")
            val view: ViewGroup = param.thisObject as ViewGroup
            view.dumpChild()
            val contentView = view.findChild("app:id/status_bar_contents")
            logger.d("status_bar_contents: $contentView")
            kotlin.runCatching {
                // Android it's Linear, Oneui6 is Relative.
                val linearRoot = contentView as ViewGroup
                linearRoot.addView(StatusBarClipView(contentView.context), 0)
                logger.d("Chip added.")
            }.onFailure {
                logger.e(it, "Fail to add chip")
            }
        }

        afterConstruct(
            clazz = Reflections.findClass(
                "com.android.systemui.statusbar.phone.AutoHideControllerImpl",
                lpparam.classLoader
            ),
        ) { param ->
            logger.d("AutoHideControllerImpl: ${param.thisObject}")
            sAutoHideControllerImpl = param.thisObject
        }
    }
}.onFailure {
    logger.e(it, "hookView")
}