package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ClipData
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.protobuf.Message
import tornaco.apps.shortx.core.BuildPropInternal
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
                require(file.exists() && file.isFile && file.canRead()) {
                    "File is not readable: $filePath"
                }
                val uri = file.toShortXContentUri()
                val ext = MimeTypeMap.getFileExtensionFromUrl(filePath)
                val mimeType = if (ext.isNotBlank()) {
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
                } else null
                val label = data.note?.ifBlank { file.name } ?: file.name
                val clipData = ClipData(
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

private fun File.toShortXContentUri(): Uri {
    val relativePath = canonicalPath.removePrefix(File.separator)
    return Uri.Builder()
        .scheme("content")
        .authority(BuildPropInternal.APP_PACKAGE_NAME)
        .appendPath("root")
        .appendEncodedPath(Uri.encode(relativePath, "/"))
        .build()
}
