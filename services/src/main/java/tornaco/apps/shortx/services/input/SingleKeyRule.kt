package tornaco.apps.shortx.services.input

import android.app.ActivityThread
import android.view.KeyEvent
import com.android.dx.stock.ProxyBuilder
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.platform.Classes.singleKeyGestureDetector_SingleKeyRule
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.data.Dirs
import java.lang.reflect.Method

object SingleKeyRuleInsMaker {
    private val logger = Logger("SingleKeyRuleInsMaker")

    fun make() {
        val at = ActivityThread.currentActivityThread()
        logger.w("ActivityThread.currentActivityThread()= $at")
        if (at == null) {
            return
        }
        val application = at.application
        logger.w("ActivityThread.getApplication()= $application")
        val loadedApk = application.mLoadedApk
        logger.w("ActivityThread.loadedApk()= $loadedApk")

        val classLoader = loadedApk.classLoader
        logger.w("ActivityThread.classLoader= $classLoader")

        val ruleClass = classLoader.singleKeyGestureDetector_SingleKeyRule()
        // TODO Check
        // 找到构造方法高版本：SingleKeyRule(int keyCode)
        // 找到构造方法：SingleKeyRule(int keyCode, int supportedGestures)
        val constructor = ruleClass.getDeclaredConstructor(
            Int::class.javaPrimitiveType,
        )
        constructor.isAccessible = true
        // 使用 DexMaker ProxyBuilder 创建子类
        val ruleInstance = ProxyBuilder
            .forClass(ruleClass)
            .dexCache(Dirs.dexCacheDir().apply {
                mkdirs()
            })
            .withSharedClassLoader()
            .markTrusted()
            .constructorArgTypes(Int::class.javaPrimitiveType)
            .constructorArgValues(KeyEvent.KEYCODE_VOLUME_UP)
            .handler { proxy: Any?, method: Method, args: Array<Any> ->
                val name = method.name
                logger.logOnDebug("[SingleKeyRule] Called method: $name")
                if (name == "toString") return@handler "SingleKeyRule-Ins"
                return@handler method.invoke(proxy, *args) // 默认行为
            }
            .build()
        // 用于调试：打印实例类名
        logger.logOnDebug("Created SingleKeyRule instance: $ruleInstance")
    }
}
