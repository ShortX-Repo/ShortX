package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.protobuf.Message
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File


object ShareContent : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShareContent::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        return with(params) {
            val cv = RuleContextAndVars(params)
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.ShareContent::class.java

            val text = data.text.compileContextAndVars(cv)
            val filePath = data.filePath.compileContextAndVars(cv)
            val mimeTypeOverride = data.mimeType.compileContextAndVars(cv)
            val chooserTitle = data.chooserTitle.compileContextAndVars(cv)

            val intent = Intent(Intent.ACTION_SEND).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                if (filePath.isNotBlank()) {
                    val file = File(filePath)
                    require(file.exists() && file.isFile && file.canRead()) {
                        "File is not readable: $filePath"
                    }
                    val uri = file.toShortXContentUri()
                    val ext = MimeTypeMap.getFileExtensionFromUrl(filePath)
                    val detectedMimeType = if (ext.isNotBlank()) {
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
                    } else null

                    type = mimeTypeOverride.ifBlank { detectedMimeType ?: "*/*" }
                    putExtra(Intent.EXTRA_STREAM, uri)
                    clipData = ClipData.newUri(androidContext.contentResolver, file.name, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    if (text.isNotBlank()) {
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                } else {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
            }

            val chooser = Intent.createChooser(
                intent,
                chooserTitle.ifBlank { "Share" }
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            androidContext.startActivity(chooser)

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
