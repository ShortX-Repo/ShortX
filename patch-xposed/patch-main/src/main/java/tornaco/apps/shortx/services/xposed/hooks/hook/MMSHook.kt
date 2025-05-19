package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.Telephony.Mms
import android.provider.Telephony.Sms
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.phone.ISMSProvider
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import util.Reflections
import java.util.Locale


object MMSHook {
    private val logger = Logger("ShortXHook.MMSHook")

    fun hook(lpparam: IPackageLoaded.Param) {
        if (lpparam.packageName == "com.android.providers.telephony") {
            afterMethod(
                Reflections.findClass(
                    "com.android.providers.telephony.SmsProvider",
                    lpparam.classLoader
                ), "onCreate"
            ) {
                val provider = it.thisObject as ContentProvider
                logger.d("onCreate: $provider")
                ShortXManagerNative.shortX?.attachSMSProvider(
                    SMSProviderImpl(
                        requireNotNull(
                            provider.context
                        ) { "Context is null." }
                    )
                )
            }
        }
    }

    internal class SMSProviderImpl(private val context: Context) : ISMSProvider.Stub() {
        private val handler = newHandlerOfNewThread("SXSMSProvider")
        override fun markAsRead(id: Long, receiver: SynchronousResultReceiver) {
            handler.post {
                val res: String = kotlin.runCatching {
                    getConversationId(context, id)?.let {
                        updateSmsReadStatus(
                            context,
                            it
                        )
                    }
                    markSmsAsRead(context, id)
                    true.toString()
                }.getOrElse {
                    it.stackTraceToString()
                }
                logger.d("markSmsAsRead: $id $res")
                receiver.send(
                    ByteArrayWrapper(
                        StringPair.newBuilder()
                            .setFirst("result")
                            .setSecond(res).build()
                            .toByteArray()
                    )
                )
            }
        }

        override fun delete(id: Long, receiver: SynchronousResultReceiver) {
            handler.post {
                val res: String = kotlin.runCatching {
                    deleteSms(context, id)
                    true.toString()
                }.getOrElse {
                    it.stackTraceToString()
                }
                logger.d("delete: $id $res")
                receiver.send(
                    ByteArrayWrapper(
                        StringPair.newBuilder()
                            .setFirst("result")
                            .setSecond(res).build()
                            .toByteArray()
                    )
                )
            }
        }
    }

    private fun markSmsAsRead(context: Context, smsId: Long): Boolean {
        val uri: Uri = Sms.CONTENT_URI
        val values = ContentValues().apply {
            put(Sms.READ, true)
            put(Sms.SEEN, true)
        }
        val rowsUpdated = context.contentResolver.update(
            uri,
            values,
            "_id=?",
            arrayOf(smsId.toString())
        )
        logger.d("markSmsAsRead: $smsId $rowsUpdated")
        return rowsUpdated > 0
    }

    private fun deleteSms(context: Context, smsId: Long): Boolean {
        val uri: Uri = Sms.CONTENT_URI
        val rowsDeleted = context.contentResolver.delete(
            uri,
            "_id=?",
            arrayOf(smsId.toString())
        )

        return rowsDeleted > 0
    }

    fun getConversationId(context: Context, smsId: Long): Long? {
        val uri = Uri.parse("content://sms")
        val projection = arrayOf("thread_id")
        val selection = "_id = ?"
        val selectionArgs = arrayOf(smsId.toString())

        val cursor = context.contentResolver.query(
            uri, projection, selection, selectionArgs, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val threadIdIndex = it.getColumnIndex("thread_id")
                return if (threadIdIndex != -1) {
                    it.getLong(threadIdIndex) // 获取会话ID
                } else {
                    null
                }
            }
        }
        return null
    }


    // 根据thread_id标记短信为已读
    private fun markConversationAsRead(context: Context, threadId: Long): Boolean {
        val uri = Uri.parse("content://sms")
        val contentValues = ContentValues().apply {
            put(Sms.READ, 1)  // 设置为已读
        }

        val selection = "thread_id = ?"  // 只更新未读短信
        val selectionArgs = arrayOf(threadId.toString())

        val rowsUpdated = context.contentResolver.update(
            uri,
            contentValues,
            selection,
            selectionArgs
        )
        logger.d("markConversationAsRead: $threadId $rowsUpdated")

        return rowsUpdated > 0
    }

    /** Update the read status of SMS/MMS messages by thread and timestamp
     *  @param threadId The thread of sms/mms to change
     *  @param timestampInMillis Change the status before this timestamp
     *  */
    fun updateSmsReadStatus(
        context: Context,
        threadId: Long,
        timestampInMillis: Long = Long.MAX_VALUE
    ) {
        logger.d("updateSmsReadStatus: $threadId $timestampInMillis")
        val resolver: ContentResolver = context.contentResolver
        val values = ContentValues()
        values.put("read", 1)
        values.put("seen", 1) /* If you read it you saw it */
        val smsSelection = java.lang.String.format(
            Locale.US,
            "%s=%d AND %s<=%d AND %s=0",
            Sms.THREAD_ID,
            threadId,
            Sms.DATE,
            timestampInMillis,
            Sms.READ
        )
        resolver.update(Sms.CONTENT_URI, values, smsSelection, null /*selectionArgs*/)
        val mmsSelection = java.lang.String.format(
            Locale.US,
            "%s=%d AND %s<=%d AND %s=0",
            Mms.THREAD_ID,
            threadId,
            Mms.DATE,
            timestampInMillis / 1000L,
            Mms.READ
        )
        resolver.update(Mms.CONTENT_URI, values, mmsSelection, null /*selectionArgs*/)
    }
}

