package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ClipData
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File


object WriteClipboard : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WriteClipboard::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        return with(params) {
            val cv = RuleContextAndVars(params)
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.WriteClipboard::class.java

            val filePath = data.filePath.compileContextAndVars(cv)

            if (filePath.isNotBlank()) {
                val file = File(filePath)
                val uri = FileProvider.getUriForFile(
                    androidContext,
                    "tornaco.apps.shortx",
                    file
                )
                val ext = MimeTypeMap.getFileExtensionFromUrl(filePath)
                val mimeType = if (ext.isNotBlank()) {
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
                } else null
                val label = data.note?.ifBlank { file.name } ?: file.name
                val clipData = ClipData(
                    androidContext.clipboardManager,
                    label,
                    arrayOf(mimeType ?: "*/*"),
                    ClipData.Item(uri)
                )
                androidContext.clipboardManager.setPrimaryClip(clipData)
            } else {
                androidContext.clipboardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        data.note ?: "shortx-action",
                        data.text.compileContextAndVars(cv)
                    )
                )
            }
            Actor.Res.EMPTY
        }
    }
}