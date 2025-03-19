package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.media.FileRow
import tornaco.apps.shortx.core.platform.Classes.mediaProviderClass
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.Methods.onDelete
import tornaco.apps.shortx.core.platform.Methods.onInsert
import tornaco.apps.shortx.core.platform.PackageNames.mediaProvider
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.logAdapter
import util.Reflections
import java.lang.reflect.Method
import java.lang.reflect.Proxy

// Happen on Media provider process
object MediaProviderHook {
    private val logger = Logger("ShortXHook.MediaProviderHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == mediaProvider) {
            // Install log
            logAdapter = { _: Int, tag: String, msg: String ->
                XposedBridge.log(tag + "\t" + msg)
            }

            beforeMethod(
                lpparam.classLoader.mediaProviderClass(), onCreate
            ) { param ->
                runCatching {
                    logger.w("onCreate.")
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
                                handleOnDelete(args)
                            }
                            if (onInsert == method.name) {
                                handleOnInsert(args)
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

    private fun handleOnInsert(args: Array<Any?>?) {
        runCatching {
            val fileRow = mapToFileRow(args)
            fileRow?.let {
                logger.d("handleOnInsert, shortx: ${ShortXManagerNative.shortX}")
                ShortXManagerNative.shortX?.onMediaStoreInsert(it)
            }
        }.onFailure {
            logger.e(it, "handleOnInsert")
        }
    }

    private fun handleOnDelete(args: Array<Any?>?) {
        runCatching {
            val fileRow = mapToFileRow(args)
            fileRow?.let {
                logger.d("handleOnDelete, shortx: ${ShortXManagerNative.shortX}")
                ShortXManagerNative.shortX?.onMediaStoreDelete(it)
            }
        }.onFailure {
            logger.e(it, "handleOnDelete")
        }
    }

    private fun mapToFileRow(args: Array<Any?>?): FileRow? {
        return runCatching {
            if (args == null) return null
            // @NonNull FileRow deletedRow
            val deletedRow = args[1]
            val id = Reflections.callMethod(deletedRow, "getId") as Long
            val type = Reflections.callMethod(deletedRow, "getMediaType") as Int
            val volumeName = Reflections.callMethod(deletedRow, "getVolumeName") as String?
            val ownerPackageName =
                Reflections.callMethod(deletedRow, "getOwnerPackageName") as String?
            val path = Reflections.callMethod(deletedRow, "getPath") as String?
            val mediaUri: String? = getUriFrom(volumeName, type, id)?.toString()
            FileRow(
                id = id,
                volumeName = volumeName.orEmpty(),
                mediaType = type,
                ownerPackageName = ownerPackageName.orEmpty(),
                path = path.orEmpty(),
                mediaUri = mediaUri
            )
        }.getOrElse {
            logger.e(it, "mapToFileRow")
            null
        }
    }

    private fun getUriFrom(volumeName: String?, type: Int, id: Long): Uri? = runCatching {
        if (MediaStore.VOLUME_INTERNAL == volumeName) return null

        val mediaType = when (type) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                TYPE_IMAGE
            }

            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                TYPE_VIDEO
            }

            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> {
                TYPE_AUDIO
            }

            MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT -> {
                TYPE_DOCUMENT
            }

            else -> null
        }
        val mediaUri = mediaType?.let {
            getUriForDocumentId(getDocIdForIdent(it, id))
        }
        return mediaUri
    }.getOrElse {
        logger.e(it, "getUriFrom")
        null
    }

}


private fun getDocIdForIdent(type: String, id: Long): String {
    return "$type:$id"
}

private const val TYPE_IMAGE = "image"
private const val TYPE_VIDEO = "video"
private const val TYPE_AUDIO = "audio"
private const val TYPE_DOCUMENT = "document";

private class Ident {
    var type: String? = null
    var id: Long = 0
}

private fun getIdentForDocId(docId: String): Ident {
    val ident = Ident()
    val split = docId.indexOf(':')
    if (split == -1) {
        ident.type = docId
        ident.id = -1
    } else {
        ident.type = docId.substring(0, split)
        ident.id = docId.substring(split + 1).toLong()
    }
    return ident
}

private fun getUriForDocumentId(docId: String): Uri? {
    val ident = getIdentForDocId(docId)
    return if (TYPE_IMAGE == ident.type && ident.id != -1L) {
        ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ident.id
        )
    } else if (TYPE_VIDEO == ident.type && ident.id != -1L) {
        ContentUris.withAppendedId(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, ident.id
        )
    } else if (TYPE_AUDIO == ident.type && ident.id != -1L) {
        ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ident.id
        )
    } else {
        null
    }
}