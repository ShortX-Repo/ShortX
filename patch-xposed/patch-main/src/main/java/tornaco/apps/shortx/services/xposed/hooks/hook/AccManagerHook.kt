package tornaco.apps.shortx.services.xposed.hooks.hook

import android.accessibilityservice.AccessibilityServiceInfo
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.accManager
import tornaco.apps.shortx.core.platform.Classes.accProxyManager
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object AccManagerHook {
    private val logger = Logger("ShortXHook.AccManagerHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookSuppressingAccessibilityServices(lpparam)
        // hookProxyManager(lpparam)
    }

    private fun hookSuppressingAccessibilityServices(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            afterMethod(
                lpparam.classLoader.accManager(),
                "suppressingAccessibilityServicesLocked"
            ) { param ->
                logger.d("suppressingAccessibilityServicesLocked ${param.result}")
                if (param.result.toString().toBoolean()) {
                    logger.i("Change suppressingAccessibilityServicesLocked result to false")
                    param.result = false
                }
            }
        }.onFailure {
            logger.e(it, "hookSuppressingAccessibilityServices")
        }

    private fun hookProxyManager(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            afterConstruct(
                lpparam.classLoader.accProxyManager(),
            ) { param ->
                // AccessibilityUserState
                // final List<AccessibilityServiceInfo> mInstalledServices = new ArrayList<>();
                val list = Reflections.getObjectField(param.thisObject, "mInstalledServices")
                logger.w("list= $list")
                val proxyList = ProxyList()
                proxyList.addAll(list as Collection<AccessibilityServiceInfo>)
                Reflections.setObjectField(param.thisObject, "mInstalledServices", proxyList)
            }
        }.onFailure {
            logger.e(it, "hookProxyManager")
        }
}

class ProxyList : ArrayList<AccessibilityServiceInfo>() {
    private val logger = Logger("ProxyList")

    override fun addAll(elements: Collection<AccessibilityServiceInfo>): Boolean {
        logger.w("addAll: ${elements.map { it }} ${Throwable().stackTraceToString()}")
        return super.addAll(elements)
    }

    override fun add(element: AccessibilityServiceInfo): Boolean {
        logger.w("add: $element ${Throwable().stackTraceToString()}")
        return super.add(element)
    }

    override fun remove(element: AccessibilityServiceInfo): Boolean {
        logger.w("remove: $element ${Throwable().stackTraceToString()}")
        return super.remove(element)
    }

    override fun clear() {
        logger.w("clear: ${Throwable().stackTraceToString()}")
        super.clear()
    }

    override fun removeAll(elements: Collection<AccessibilityServiceInfo>): Boolean {
        logger.w("removeAll: ${elements.map { it }} ${Throwable().stackTraceToString()}")
        return super.removeAll(elements)
    }

    override fun removeAt(p0: Int): AccessibilityServiceInfo {
        logger.w("removeAt: $p0 ${Throwable().stackTraceToString()}")
        return super.removeAt(p0)
    }
}