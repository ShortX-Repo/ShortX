package tornaco.apps.shortx.services.phone.sms

import android.content.ContentValues
import android.content.Context
import android.net.Uri

object MarkAsRead {
    fun markSmsAsRead(context: Context, smsId: Long): Boolean {
        val uri = Uri.parse("content://sms/inbox")

        val values = ContentValues().apply {
            put("read", 1)
        }

        val rowsUpdated = context.contentResolver.update(
            uri,
            values,
            "_id=?",
            arrayOf(smsId.toString())
        )

        return rowsUpdated > 0
    }
}