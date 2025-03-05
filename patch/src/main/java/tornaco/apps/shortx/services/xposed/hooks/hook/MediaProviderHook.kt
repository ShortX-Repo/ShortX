package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.platform.Classes.mediaDocumentProviderClass
import tornaco.apps.shortx.core.platform.Methods.onMediaStoreDelete
import tornaco.apps.shortx.core.platform.Methods.onMediaStoreInsert
import tornaco.apps.shortx.core.platform.PackageNames.mediaProvider
import tornaco.apps.shortx.core.util.FileHelper
import tornaco.apps.shortx.core.util.Logger

// Happen on Media provider process
object MediaProviderHook {
    private val logger = Logger("ShortXHook.MediaProviderHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == mediaProvider) {
            // static void onMediaStoreInsert(Context context, String volumeName, int type, long id)
            afterMethod(
                lpparam.classLoader.mediaDocumentProviderClass(), onMediaStoreInsert
            ) { param ->
                val mediaUri = getUriFromParams(param)
                val path = FileHelper.getRealPathFromURI(param.args[0] as Context, mediaUri)
                logger.logOnDebug("onMediaStoreInsert: $mediaUri --- $path")
                mediaUri?.let {
                    logger.d("onMediaStoreInsert, shortx: ${ShortXManagerNative.shortX}")
                    ShortXManagerNative.shortX?.onMediaStoreInsert(mediaUri, path)
                }
            }

            beforeMethod(
                lpparam.classLoader.mediaDocumentProviderClass(), onMediaStoreDelete
            ) { param ->
                val mediaUri: Uri? = getUriFromParams(param)
                val path: String? = FileHelper.getRealPathFromURI(param.args[0] as Context, mediaUri)
                logger.logOnDebug("onMediaStoreDelete: $mediaUri --- $path")
                mediaUri?.let {
                    ShortXManagerNative.shortX?.onMediaStoreDelete(mediaUri, path)
                }
            }
        }
    }


    private fun getUriFromParams(param: XC_MethodHook.MethodHookParam): Uri? = runCatching {
        val volumeName = param.args[1] as String
        val type = param.args[2] as Int
        val id = param.args[3] as Long

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
        logger.e(it, "getUriFromParams")
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