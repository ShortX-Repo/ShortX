package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ContentProvider
import android.content.Context
import android.net.Uri
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.media.FileRow
import tornaco.apps.shortx.core.platform.Classes.mediaDocumentProviderClass
import tornaco.apps.shortx.core.platform.Classes.mediaProviderClass
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.Methods.onDelete
import tornaco.apps.shortx.core.platform.Methods.onMediaStoreInsert
import tornaco.apps.shortx.core.platform.PackageNames.mediaProviderPackages
import tornaco.apps.shortx.core.util.FileHelper
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.logAdapter
import util.Reflections
import java.lang.reflect.Method
import java.lang.reflect.Proxy

// Happen on Media provider process
object MediaProviderHook {
    private val logger = Logger("ShortXHook.MediaProviderHook")
    private const val FILE_LISTENER_CLASS =
        "com.android.providers.media.DatabaseHelper.OnFilesChangeListener"

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (mediaProviderPackages.contains(lpparam.packageName)) {
            // Install log
            logAdapter = { _: Int, tag: String, msg: String ->
                if (BuildPropInternal.APP_IS_DEBUG) {
                    XposedBridge.log(tag + "\t" + msg)
                }
            }

            hookMediaStore(lpparam)

            val hasFileListenerClass = runCatching {
                Reflections.findClass(
                    FILE_LISTENER_CLASS,
                    lpparam.classLoader
                )
            }.getOrNull() != null
            logger.w("hasFileListenerClass: $hasFileListenerClass")

            if (hasFileListenerClass) {
                hookOnCreate(lpparam)
            }
        }
    }

    // For Insert.
    private fun hookMediaStore(lpparam: XC_LoadPackage.LoadPackageParam) {
        fun getUriFromParams(param: XC_MethodHook.MethodHookParam): Uri? {
            val volumeName = param.args[1] as String
            val type = param.args[2] as Int
            val id = param.args[3] as Long
            return MediaProviderUtil.getUriFrom(volumeName, type, id)
        }

        // static void onMediaStoreInsert(Context context, String volumeName, int type, long id)
        afterMethod(
            lpparam.classLoader.mediaDocumentProviderClass(), onMediaStoreInsert
        ) { param ->
            val mediaUri = getUriFromParams(param)
            val path: String? = FileHelper.getRealPathFromURI(param.args[0] as Context, mediaUri)
            logger.logOnDebug("onMediaStoreInsert: $mediaUri --- $path")
            mediaUri?.let {
                logger.d("onMediaStoreInsert, shortx: ${ShortXManagerNative.shortX}")
                val fileRow = FileRow(0, "", 0, "", path.orEmpty(), mediaUri.toString())
                ShortXManagerNative.shortX?.onMediaStoreInsert(fileRow)
            }
        }
    }

    // For delete.
    private fun hookOnCreate(lpparam: XC_LoadPackage.LoadPackageParam) {
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
                    FILE_LISTENER_CLASS,
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
