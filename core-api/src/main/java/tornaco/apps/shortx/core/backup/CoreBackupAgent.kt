package tornaco.apps.shortx.core.backup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.store.BaseCodeLibraryStore
import tornaco.apps.shortx.core.store.BaseDirectActionSetsStore
import tornaco.apps.shortx.core.store.BaseDirectActionSettingsStore
import tornaco.apps.shortx.core.store.BaseFunctionSettingsStore
import tornaco.apps.shortx.core.store.BaseGestureRecordsStore
import tornaco.apps.shortx.core.store.BaseGlobalVarStore
import tornaco.apps.shortx.core.store.BasePkgSetsStore
import tornaco.apps.shortx.core.store.BaseRuleSetsStore
import tornaco.apps.shortx.core.store.BaseRuleSettingsStore
import tornaco.apps.shortx.core.store.BaseStatusBarSettingsStore
import tornaco.apps.shortx.core.store.BaseToggleSettingsStore
import tornaco.apps.shortx.core.settings.defaultStatusBarTileSetting
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.ZipUtils
import java.io.File

fun autoGenBackupFileName() =
    "ShortX-Backup-${DateUtils.formatForFileName(System.currentTimeMillis())}"

interface BackupSettings {
    val cacheDir: File
    val destDir: File
    val versionCode: Long
}

open class CoreBackupAgent(private val settings: BackupSettings) {
    private val logger = Logger("CoreBackupAgent")
    private val shortX by lazy { shortXManager }

    suspend fun backup(fileName: String? = null): File {
        return withContext(Dispatchers.IO) {
            val backupTempDir = File(settings.cacheDir, "backup-${System.currentTimeMillis()}")

            // Rules
            val allRules = shortX.getAllRules()
            val ruleStore = BaseRuleSettingsStore(this, File(backupTempDir, RULE_FILE_NAME))
            ruleStore.addRules(rules = allRules, write = false)
            ruleStore.write()

            // DAs
            val allDAs = shortX.getAllDirectAction()
            val daStore = BaseDirectActionSettingsStore(this, File(backupTempDir, DA_FILE_NAME))
            daStore.addDirectActions(das = allDAs, write = false)
            daStore.write()

            // Code libraries
            val allCodeLibraries = shortX.getAllCodeLibraryItems()
            val codeLibraryStore =
                BaseCodeLibraryStore(this, File(backupTempDir, CODE_LIBRARY_FILE_NAME))
            codeLibraryStore.addAllCodeLibraryItems(allCodeLibraries, write = false)
            codeLibraryStore.write()

            // Toggles
            val allToggles = shortX.getAllToggles()
            val toggleStore = BaseToggleSettingsStore(this, File(backupTempDir, TOGGLE_FILE_NAME))
            toggleStore.addToggles(das = allToggles, write = false)
            toggleStore.write()

            // PkgSets
            val allPkgSets = shortX.getAllPkgSets(true)
            val pkgSetStore = BasePkgSetsStore(this, File(backupTempDir, PKGSET_FILE_NAME))
            pkgSetStore.addAllPkgSets(pkgSets = allPkgSets, write = false)
            pkgSetStore.write()

            // GV
            val allGV = shortX.getAllGlobalVars().filterNot { it.isSecret }
            val gvStore = BaseGlobalVarStore(this, File(backupTempDir, GV_FILE_NAME))
            gvStore.addAllGlobalVars(globalVars = allGV, write = false)
            gvStore.write()

            // DA Sets
            val allDASets = shortX.getAllDASets(false)
            val daSetsStore =
                BaseDirectActionSetsStore(this, File(backupTempDir, DA_SET_FILE_NAME))
            daSetsStore.addDirectActionSets(directActionSets = allDASets, write = false)
            daSetsStore.write()

            // Rule Sets
            val allRuleSets = shortX.getAllRuleSets(false)
            val ruleSetStore = BaseRuleSetsStore(this, File(backupTempDir, RULE_SET_FILE_NAME))
            ruleSetStore.addRuleSets(ruleSets = allRuleSets, write = false)
            ruleSetStore.write()

            // Gestures
            val allGestures = shortX.getAllGestureRecords(false)
                .mapNotNull {
                    shortX.getGestureRecordById(it.id)
                }

            val gestureSetStore =
                BaseGestureRecordsStore(this, File(backupTempDir, GESTURE_RECORD_FILE_NAME))
            gestureSetStore.addAllGestureRecords(records = allGestures, write = false)
            gestureSetStore.write()

            // Functions
            val allFunctions = shortX.getAllFunctions()
            val functionStore = BaseFunctionSettingsStore(this, File(backupTempDir, FUNCTION_FILE_NAME))
            functionStore.addFunctions(functions = allFunctions, write = false)
            functionStore.write()

            // Status bar tiles
            val allStatusBarTiles = shortX.getAllStatusBarTileSettings()
            val statusBarTileStore =
                BaseStatusBarSettingsStore(this, File(backupTempDir, STATUS_BAR_TILE_FILE_NAME))
            statusBarTileStore.addAllSettings(records = allStatusBarTiles, write = false)
            statusBarTileStore.write()

            // Meta
            val metaFile = File(backupTempDir, META_FILE_NAME)
            val meta = "${settings.versionCode}"
            metaFile.writeText(meta)

            val backupZipRootDir = settings.destDir

            val name = (fileName ?: autoGenBackupFileName()) + ".zip"

            ZipUtils.zip(backupTempDir.absolutePath, backupZipRootDir.absolutePath, name)

            backupTempDir.deleteRecursively()

            return@withContext File(backupZipRootDir, name)
        }
    }

    suspend fun resetAllData() {
        logger.w("resetAllData")
        withContext(Dispatchers.IO) {
            shortX.getAllRules().forEach {
                logger.w("Delete rule: ${it.title}")
                shortX.deleteRule(it.id)
            }
            shortX.getAllDirectAction().forEach {
                logger.w("Delete da: ${it.title}")
                shortX.deleteDirectAction(it.id)
            }
            shortX.getAllToggles().forEach {
                logger.w("Delete toggle: ${it.title}")
                shortX.deleteToggle(it.id)
            }
            shortX.getAllGlobalVars().forEach {
                logger.w("Delete gv: ${it.name}")
                shortX.deleteGlobalVar(it.name)
            }
            shortX.getAllPkgSets(withPkgList = false).forEach {
                logger.w("Delete pkg set: ${it.label}")
                shortX.deletePkgSet(it.label)
            }
            shortX.getAllRuleSets(false).forEach {
                logger.w("Delete rule set: ${it.title}")
                shortX.deleteRuleSet(it.id)
            }
            shortX.getAllDASets(false).forEach {
                logger.w("Delete da set: ${it.title}")
                shortX.deleteDASet(it.id)
            }
            shortX.getAllFunctions().forEach {
                logger.w("Delete function: ${it.name}")
                shortX.deleteFunction(it.id)
            }
            StatusBarTile.entries.filterNot { it == StatusBarTile.UNRECOGNIZED }.forEach {
                logger.w("Reset tile setting: $it")
                shortX.updateStatusBarTileSetting(defaultStatusBarTileSetting(it))
            }
            shortX.clearEvaluateRecords()
        }
    }

}