package tornaco.apps.shortx.ui.backup

import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.backup.DA_FILE_NAME
import tornaco.apps.shortx.core.backup.META_FILE_NAME
import tornaco.apps.shortx.core.backup.RULE_FILE_NAME
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.store.BaseDirectActionSettingsStore
import tornaco.apps.shortx.core.store.BaseRuleSettingsStore
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.ZipUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.zip.ZipInputStream

class BackupAgent(private val context: Context) {
    private val logger = Logger("BackupAgent")

    suspend fun backup(): File {
        return withContext(Dispatchers.IO) {
            val backupTempDir = File(context.cacheDir, "backup-${System.currentTimeMillis()}")

            // Rules
            val allRules = shortXManager.getAllRules()
            val ruleStore = BaseRuleSettingsStore(this, File(backupTempDir, RULE_FILE_NAME))
            ruleStore.addRules(rules = allRules, write = false)
            ruleStore.write()

            // DAs
            val allDAs = shortXManager.getAllDirectAction()
            val daStore = BaseDirectActionSettingsStore(this, File(backupTempDir, DA_FILE_NAME))
            daStore.addDirectActions(das = allDAs, write = false)
            daStore.write()

            // Meta
            val metaFile = File(backupTempDir, META_FILE_NAME)
            val meta = "${BuildProp.APP_VERSION_CODE}"
            metaFile.writeText(meta)

            val backupZipRootDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            val name =
                "ShortX-Backup-${DateUtils.formatForFileName(System.currentTimeMillis())}.zip"

            ZipUtils.zip(backupTempDir.absolutePath, backupZipRootDir.absolutePath, name)

            backupTempDir.deleteRecursively()

            return@withContext File(backupZipRootDir, name)
        }
    }

    suspend fun restore(uri: Uri) {
        logger.d("restore, uri: $uri")
        context.contentResolver.openInputStream(uri).use { input ->
            if (input == null) throw FileNotFoundException("openInputStream failed")
            restore(input)
        }
    }

    suspend fun restore(assetName: String) {
        logger.d("restore, assetName: $assetName")
        context.assets.open(assetName).use { input ->
            restore(input)
        }
    }

    suspend fun restore(input: InputStream) {
        logger.d("restore: $input")
        withContext(Dispatchers.IO) {
            val tmpZipFile = File(context.cacheDir, "shortx-backup-tmp.zip")
            val tmpExtractedFile = File(context.cacheDir, "shortx-backup-tmp")

            tmpZipFile.outputStream().use {
                input.copyTo(it)
            }
            ZipUtils.unzip(tmpZipFile.absolutePath, tmpExtractedFile.absolutePath, false)
            logger.d("upzip to tmp: $tmpExtractedFile")

            // Meta check.
            val metaFile = File(tmpExtractedFile, META_FILE_NAME)
            assert(metaFile.exists()) {
                "UNKNOWN BACKUP FILE"
            }

            // Rules
            val rulePb = File(tmpExtractedFile, RULE_FILE_NAME)
            val ruleStore = BaseRuleSettingsStore(this, rulePb)
            ruleStore.read()
            ruleStore.getAllRules().forEach {
                logger.d("restore, add rule: ${it.title}")
                // Disable this rule.
                shortXManager.addRule(Rule.newBuilder(it).setIsEnabled(false).build())
            }

            // DAs
            val daPb = File(tmpExtractedFile, DA_FILE_NAME)
            val daStore = BaseDirectActionSettingsStore(this, daPb)
            daStore.read()
            daStore.getAllDirectActions().forEach {
                logger.d("restore, add da: ${it.title}")
                shortXManager.addDirectAction(it)
            }

            // Clean up.
            tmpExtractedFile.deleteRecursively()
            tmpZipFile.deleteRecursively()
        }
    }

    private suspend fun <T> withZipFromUri(
        context: Context,
        uri: Uri, block: suspend (ZipInputStream) -> T
    ): T =
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                context.contentResolver.openInputStream(uri).use { input ->
                    if (input == null) throw FileNotFoundException("openInputStream failed")
                    ZipInputStream(input).use {
                        block.invoke(it)
                    }
                }
            }.getOrThrow()
        }
}