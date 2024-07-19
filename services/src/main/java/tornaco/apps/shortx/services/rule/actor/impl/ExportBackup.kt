package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.backup.BackupSettings
import tornaco.apps.shortx.core.backup.CoreBackupAgent
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File


object ExportBackup : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ExportBackup::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        with(params) {
            val cv = RuleContextAndVars(params)
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.ExportBackup::class.java

            val agent = CoreBackupAgent(
                object : BackupSettings {
                    override val cacheDir: File
                        get() = Dirs.backupCacheDir()
                    override val destDir: File
                        get() = Dirs.backupCacheDir()
                    override val versionCode: Long
                        get() = BuildPropInternal.APP_VERSION_CODE.toLong()
                }
            )

            val backup = agent.backup()
            logger.i("backup file: $backup")

            val destDir = data.destDir
            if (destDir.isNotBlank()) {
                val destFile = File(File(destDir.compileContextAndVars(cv)), backup.name)
                logger.d("Copy backup to $destFile")

                CopyFile.copyFileByShellCommand(
                    shortXContext = shortXContext,
                    srcFile = backup,
                    destFile = destFile,
                    deleteSrcOnSuccess = true
                )
                params.contextData.add(
                    data.customContextDataKey.forKey(ContextDataMapping.ExportBackup.backupFilePath.name)
                        .fallbackOnEmpty { ContextDataMapping.ExportBackup.backupFilePath.name },
                    destFile.absolutePath
                )
            } else {
                params.contextData.add(
                    data.customContextDataKey.forKey(ContextDataMapping.ExportBackup.backupFilePath.name)
                        .fallbackOnEmpty { ContextDataMapping.ExportBackup.backupFilePath.name },
                    backup.absolutePath
                )
            }
        }
    }
}