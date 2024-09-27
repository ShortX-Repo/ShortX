package tornaco.apps.shortx.services.audio

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import okhttp3.internal.closeQuietly
import tornaco.apps.shortx.core.proto.common.Ringtone
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug

class RingtoneLoader(private val context: Context) {
    private val logger = Logger("RingtoneLoader")

    fun load(type: Int): List<Ringtone> {
        val manager = RingtoneManager(context)
        manager.setType(type)
        val cursor = manager.cursor
        val res = mutableListOf<Ringtone>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                logger.d("Alarm name = $name")
                val uri = getUriFromCursor(context, cursor)
                logger.d("Alarm uri = $uri")
                res.add(
                    Ringtone.newBuilder()
                        .setTitle(name)
                        .setType(type)
                        .setUri(uri.toString())
                        .build()
                )
            } while (cursor.moveToNext())
        }
        cursor.closeQuietly()
        return res
    }

    private fun getUriFromCursor(context: Context, cursor: Cursor): Uri {
        val uri = ContentUris.withAppendedId(
            Uri.parse(
                cursor.getString(
                    RingtoneManager.URI_COLUMN_INDEX
                )
            ),
            cursor.getLong(RingtoneManager.ID_COLUMN_INDEX)
        )
        return runCatching {
            context.contentResolver.canonicalizeOrElse(uri)
        }.getOrElse {
            uri
        }
    }
}