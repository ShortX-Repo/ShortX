package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File

object WebDavPut : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WebDavPut::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        with(params) {
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.WebDavPut::class.java
            val cv = RuleContextAndVars(params)
            withContext(Dispatchers.IO) {
                val url = data.url.compileContextAndVars(cv)
                val localFilePath = data.localFilePath.compileContextAndVars(cv)
                val contentType = data.contentType.compileContextAndVars(cv)
                logger.d("Put $localFilePath $contentType")
                val profile = shortXContext.rule.getAllWebDavProfiles()
                    .first { it.profileId == data.profileId.compileContextAndVars(cv) }
                val sardine: Sardine = OkHttpSardine()
                sardine.setCredentials(profile.userName, profile.password)

                val tmpFile = File(
                    Dirs.baseServerTmpDir(),
                    "upload_${System.currentTimeMillis()}.tmp"
                ).apply {
                    parentFile?.mkdirs()
                }
                logger.d("tmpFile: $tmpFile")
                CopyFile.copyFile(
                    shortXContext = shortXContext,
                    srcFile = File(localFilePath),
                    destFile = tmpFile,
                    owner = "1000.1000"
                )

                sardine.put(url, tmpFile, contentType, false)
                logger.d("Put success.")
                runCatching { tmpFile.deleteRecursively() }
            }
        }
    }
}