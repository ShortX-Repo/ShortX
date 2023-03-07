package tornaco.apps.shortx.ui.backup

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.store.BaseRuleSettingsStore
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.ZipUtils
import java.io.File

class BackupAgent(private val context: Context) {
    suspend fun backup(): File {
        return withContext(Dispatchers.IO) {
            val allRules = shortXManager.getAllRules()

            val backupTempDir = File(context.cacheDir, "backup-${System.currentTimeMillis()}")
            val ruleStore = BaseRuleSettingsStore(this, File(backupTempDir, "rule.pb"))

            ruleStore.addRules(rules = allRules, write = false)
            ruleStore.write()

            val backupZipRootDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            val name =
                "ShortX-Backup-${DateUtils.formatForFileName(System.currentTimeMillis())}.zip"

            ZipUtils.zip(backupTempDir.absolutePath, backupZipRootDir.absolutePath, name)

            return@withContext File(backupZipRootDir, name)
        }
    }
}