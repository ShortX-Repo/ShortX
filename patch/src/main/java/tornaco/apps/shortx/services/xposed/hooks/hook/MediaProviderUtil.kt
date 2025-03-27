package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import tornaco.apps.shortx.core.media.FileRow
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object MediaProviderUtil {
    private val logger = Logger("ShortXHook.MediaProviderHook")

    fun mapToFileRowOnDelete(args: Array<Any?>?, context: Context): FileRow? {
        return if (Build.VERSION.SDK_INT >= 32) {
            mapToFileRow121AndAbove(args)
        } else {
            mapToFileRow12OrBelowOnDelete(args)
        }
    }

    private fun mapToFileRow121AndAbove(args: Array<Any?>?): FileRow? {
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

    private fun mapToFileRow12OrBelowOnDelete(args: Array<Any?>?): FileRow? {
        // Android 12
        //  public interface OnFilesChangeListener {
        //        public void onInsert(@NonNull DatabaseHelper helper, @NonNull String volumeName, long id,
        //                int mediaType, boolean isDownload);
        //        public void onUpdate(@NonNull DatabaseHelper helper, @NonNull String volumeName,
        //                long oldId, int oldMediaType, boolean oldIsDownload,
        //                long newId, int newMediaType, boolean newIsDownload,
        //                String oldOwnerPackage, String newOwnerPackage, String oldPath);
        //        public void onDelete(@NonNull DatabaseHelper helper, @NonNull String volumeName, long id,
        //                int mediaType, boolean isDownload, String ownerPackage, String path);
        //    }
        return runCatching {
            if (args == null) return null
            val id = args[2] as Long
            val type = args[3] as Int
            val volumeName = args[1] as String?
            val ownerPackageName = ""
            val path = args[6] as String?
            val mediaUri: String? = getUriFrom(volumeName, type, id)?.toString()
            FileRow(
                id = id,
                volumeName = volumeName.orEmpty(),
                mediaType = type,
                ownerPackageName = ownerPackageName,
                path = path.orEmpty(),
                mediaUri = mediaUri
            )
        }.getOrElse {
            logger.e(it, "mapToFileRow12OrBelowOnDelete")
            null
        }
    }

    fun getUriFrom(volumeName: String?, type: Int, id: Long): Uri? = runCatching {
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
}
