package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.phone.ISMSProvider
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import util.Reflections

object MMSHook {
    private val logger = Logger("ShortXHook.MMSHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
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
        override fun markAsRead(id: Long, receiver: SynchronousResultReceiver?) {
            handler.post {
                val res: String = kotlin.runCatching {
                    markSmsAsRead(context, id).toString()
                }.getOrElse {
                    it.stackTraceToString()
                }
                logger.d("markSmsAsRead: $id $res")
                receiver?.send(
                    ByteArrayWrapper(
                        StringPair.newBuilder()
                            .setFirst("result")
                            .setSecond(res.toString()).build()
                            .toByteArray()
                    )
                )
            }
        }
    }

    private fun markSmsAsRead(context: Context, smsId: Long): Boolean {
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

