package tornaco.apps.shortx.ui.backup

import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.backup.DA_FILE_NAME
import tornaco.apps.shortx.core.backup.GV_FILE_NAME
import tornaco.apps.shortx.core.backup.META_FILE_NAME
import tornaco.apps.shortx.core.backup.PKGSET_FILE_NAME
import tornaco.apps.shortx.core.backup.RULE_FILE_NAME
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.store.BaseDirectActionSettingsStore
import tornaco.apps.shortx.core.store.BaseGlobalVarStore
import tornaco.apps.shortx.core.store.BasePkgSetsStore
import tornaco.apps.shortx.core.store.BaseRuleSettingsStore
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.ZipUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.zip.ZipInputStream

fun autoGenBackupFileName() =
    "ShortX-Backup-${DateUtils.formatForFileName(System.currentTimeMillis())}"

class BackupAgent(private val context: Context) {
    private val logger = Logger("BackupAgent")

    suspend fun backup(fileName: String? = null): File {
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

            // PkgSets
            val allPkgSets = shortXManager.getAllPkgSets(true)
            val pkgSetStore = BasePkgSetsStore(this, File(backupTempDir, PKGSET_FILE_NAME))
            pkgSetStore.addAllPkgSets(pkgSets = allPkgSets, write = false)
            pkgSetStore.write()

            // GV
            val allGV = shortXManager.getAllGlobalVars().filterNot { it.isSecret }
            val gvStore = BaseGlobalVarStore(this, File(backupTempDir, GV_FILE_NAME))
            gvStore.addAllGlobalVars(globalVars = allGV, write = false)
            gvStore.write()

            // Meta
            val metaFile = File(backupTempDir, META_FILE_NAME)
            val meta = "${BuildProp.APP_VERSION_CODE}"
            metaFile.writeText(meta)

            val backupZipRootDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            val name = (fileName ?: autoGenBackupFileName()) + ".zip"

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

    suspend fun resetAllData() {
        logger.w("resetAllData")
        withContext(Dispatchers.IO) {
            shortXManager.getAllRules().forEach {
                logger.w("Delete rule: ${it.title}")
                shortXManager.deleteRule(it.id)
            }
            shortXManager.getAllDirectAction().forEach {
                logger.w("Delete da: ${it.title}")
                shortXManager.deleteDirectAction(it.id)
            }
            shortXManager.getAllGlobalVars().forEach {
                logger.w("Delete gv: ${it.name}")
                shortXManager.deleteGlobalVar(it.name)
            }
            shortXManager.getAllPkgSets(withPkgList = false).forEach {
                logger.w("Delete pkg set: ${it.label}")
                shortXManager.deletePkgSet(it.label)
            }
            shortXManager.clearEvaluateRecords()
        }
    }

    suspend fun restore(input: InputStream) {
        logger.d("restore: $input")
        withContext(Dispatchers.IO) {
            val content = readBackupPackageContent(input)
            content.rules.forEach {
                logger.d("restore, add rule: ${it.title}")
                // Disable this rule.
                shortXManager.addRule(Rule.newBuilder(it).setIsEnabled(false).build())
            }
            content.das.forEach {
                logger.d("restore, add da: ${it.title}")
                shortXManager.addDirectAction(it)
            }
            content.pkgSets.forEach {
                logger.d("restore, add pkgset: ${it.label}")
                shortXManager.addPkgSet(it)
            }
            content.gvs.forEach {
                logger.d("restore, add gv: ${it.name}")
                shortXManager.addGlobalVar(it)
            }
        }
    }

    suspend fun readBackupPackageContent(assetName: String): BackupPackageContent {
        logger.d("readBackupPackageContent, assetName: $assetName")
        return context.assets.open(assetName).use { input ->
            readBackupPackageContent(input)
        }
    }

    suspend fun readBackupPackageContent(input: InputStream): BackupPackageContent {
        logger.d("readBackupPackageContent: $input")
        return withContext(Dispatchers.IO) {
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
            val rules = ruleStore.getAllRules()

            // DAs
            val daPb = File(tmpExtractedFile, DA_FILE_NAME)
            val daStore = BaseDirectActionSettingsStore(this, daPb)
            daStore.read()
            val das = daStore.getAllDirectActions()

            // PkgSets
            val pkgSetPb = File(tmpExtractedFile, PKGSET_FILE_NAME)
            val pkgSetStore = BasePkgSetsStore(this, pkgSetPb)
            pkgSetStore.read()
            val pkgSets = pkgSetStore.getAllPkgSets()

            // GVs
            val gvPb = File(tmpExtractedFile, GV_FILE_NAME)
            val gvStore = BaseGlobalVarStore(this, gvPb)
            gvStore.read()
            val gvs = gvStore.getAllGlobalVars().filterNot { it.isSecret }

            // Clean up.
            tmpExtractedFile.deleteRecursively()
            tmpZipFile.deleteRecursively()

            BackupPackageContent(das, rules, pkgSets, gvs)
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

data class BackupPackageContent(
    val das: List<DirectAction>,
    val rules: List<Rule>,
    val pkgSets: List<PkgSet>,
    val gvs: List<GlobalVar>
)