package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ContentProvider
import android.content.Context
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.platform.Classes.mediaProviderClass
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.Methods.onDelete
import tornaco.apps.shortx.core.platform.Methods.onInsert
import tornaco.apps.shortx.core.platform.PackageNames.mediaProviderPackages
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.logAdapter
import util.Reflections
import java.lang.reflect.Method
import java.lang.reflect.Proxy

// Happen on Media provider process
object MediaProviderHook {
    private val logger = Logger("ShortXHook.MediaProviderHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (mediaProviderPackages.contains(lpparam.packageName)) {
            // Install log
            logAdapter = { _: Int, tag: String, msg: String ->
                XposedBridge.log(tag + "\t" + msg)
            }
            beforeMethod(
                lpparam.classLoader.mediaProviderClass(), onCreate
            ) { param ->
                runCatching {
                    logger.w("onCreate.")
                    val provider = param.thisObject as ContentProvider
                    val context = provider.context
                    logger.w("context: $context")
                    requireNotNull(context) {
                        "Media provider context is null."
                    }
                    // import com.android.providers.media.DatabaseHelper
                    //import com.android.providers.media.DatabaseHelper.OnFilesChangeListener
                    //import com.android.providers.media.dao.FileRow
                    val mFilesListener = Reflections.getObjectField(
                        param.thisObject,
                        "mFilesListener"
                    )
                    logger.w("mFilesListener: $mFilesListener")

                    val onFilesChangeListenerClass = Reflections.findClass(
                        "com.android.providers.media.DatabaseHelper.OnFilesChangeListener",
                        lpparam.classLoader
                    )
                    val stubListener: Any? = Proxy.newProxyInstance(
                        lpparam.classLoader,
                        arrayOf(onFilesChangeListenerClass)
                    ) { _: Any?, method: Method, args: Array<Any?>? ->
                        return@newProxyInstance if ("toString" == method.name) {
                            "StubOnFilesChangeListener"
                        } else {
                            logger.d("Method called: $method (${args?.map { it }})")
                            if (onDelete == method.name) {
                                handleOnDelete(args, context)
                            }
                            if (onInsert == method.name) {
                                handleOnInsert(args, context)
                            }
                            if (args != null) {
                                Reflections.callMethod(
                                    mFilesListener,
                                    method.name,
                                    *args
                                )
                            } else {
                                Reflections.callMethod(
                                    mFilesListener,
                                    method.name
                                )
                            }
                        }
                    }
                    logger.w("stubListener: $stubListener")

                    // Install.
                    Reflections.setObjectField(param.thisObject, "mFilesListener", stubListener)
                    logger.w("StubOnFilesChangeListener installed")
                }.onFailure {
                    logger.e(it, "Handle onCreate.")
                }
            }
        }
    }

    private fun handleOnInsert(args: Array<Any?>?, context: Context) {
        runCatching {
            val fileRow = MediaProviderUtil.mapToFileRowOnInsert(args, context)
            fileRow?.let {
                logger.d("handleOnInsert, shortx: ${ShortXManagerNative.shortX}")
                ShortXManagerNative.shortX?.onMediaStoreInsert(it)
            }
        }.onFailure {
            logger.e(it, "handleOnInsert")
        }
    }

    private fun handleOnDelete(args: Array<Any?>?, context: Context) {
        runCatching {
            val fileRow = MediaProviderUtil.mapToFileRowOnDelete(args, context)
            fileRow?.let {
                logger.d("handleOnDelete, shortx: ${ShortXManagerNative.shortX}")
                ShortXManagerNative.shortX?.onMediaStoreDelete(it)
            }
        }.onFailure {
            logger.e(it, "handleOnDelete")
        }
    }
}
