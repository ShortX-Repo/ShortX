package tornaco.apps.shortx.ui.backup

import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.backup.BackupSettings
import tornaco.apps.shortx.core.backup.CoreBackupAgent
import tornaco.apps.shortx.core.backup.DA_FILE_NAME
import tornaco.apps.shortx.core.backup.DA_SET_FILE_NAME
import tornaco.apps.shortx.core.backup.GESTURE_RECORD_FILE_NAME
import tornaco.apps.shortx.core.backup.GV_FILE_NAME
import tornaco.apps.shortx.core.backup.META_FILE_NAME
import tornaco.apps.shortx.core.backup.PKGSET_FILE_NAME
import tornaco.apps.shortx.core.backup.RULE_FILE_NAME
import tornaco.apps.shortx.core.backup.RULE_SET_FILE_NAME
import tornaco.apps.shortx.core.backup.TOGGLE_FILE_NAME
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.store.BaseDirectActionSetsStore
import tornaco.apps.shortx.core.store.BaseDirectActionSettingsStore
import tornaco.apps.shortx.core.store.BaseGestureRecordsStore
import tornaco.apps.shortx.core.store.BaseGlobalVarStore
import tornaco.apps.shortx.core.store.BasePkgSetsStore
import tornaco.apps.shortx.core.store.BaseRuleSetsStore
import tornaco.apps.shortx.core.store.BaseRuleSettingsStore
import tornaco.apps.shortx.core.store.BaseToggleSettingsStore
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.ZipUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.zip.ZipInputStream


class BackupAgent(private val context: Context) : CoreBackupAgent(
    object : BackupSettings {
        override val cacheDir: File
            get() = context.cacheDir
        override val destDir: File
            get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        override val versionCode: Long
            get() = BuildPropInternal.APP_VERSION_CODE.toLong()
    }
) {
    private val logger = Logger("AppBackupAgent")
    private val shortX by lazy { shortXManager }

    suspend fun restore(uri: Uri, token: String?) {
        logger.d("restore, uri: $uri")
        context.contentResolver.openInputStream(uri).use { input ->
            if (input == null) throw FileNotFoundException("openInputStream failed")
            restore(input, token)
        }
    }

    suspend fun restore(assetName: String, token: String?) {
        logger.d("restore, assetName: $assetName")
        context.assets.open(assetName).use { input ->
            restore(input, token)
        }
    }

    suspend fun restore(input: InputStream, token: String?) {
        logger.d("restore: $input")
        withContext(Dispatchers.IO) {
            val content = readBackupPackageContent(input)
            content.rules.forEach {
                logger.d("restore, add rule: ${it.title}")
                // Disable this rule.
                shortX.addRule(
                    Rule.newBuilder(it).setIsEnabled(false)
                        .addAllExtras(mutableListOf<StringPair>().apply {
                            if (!token.isNullOrEmpty()) {
                                add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                            }
                        })
                        .build()
                )
            }
            content.das.forEach {
                logger.d("restore, add da: ${it.title}")
                shortX.addDirectAction(
                    DirectAction.newBuilder(it)
                        .addAllExtras(mutableListOf<StringPair>().apply {
                            if (!token.isNullOrEmpty()) {
                                add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                            }
                        })
                        .build()
                )
            }
            content.toggles.forEach {
                logger.d("restore, add toggle: ${it.title}")
                shortX.addToggle(
                    Toggle.newBuilder(it)
                        .addAllExtras(mutableListOf<StringPair>().apply {
                            if (!token.isNullOrEmpty()) {
                                add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                            }
                        })
                        .build()
                )
            }
            content.pkgSets.forEach {
                logger.d("restore, add pkgset: ${it.label}")
                shortX.addPkgSet(it)
            }
            content.gvs.forEach {
                logger.d("restore, add gv: ${it.name}")
                shortX.addGlobalVar(it)
            }
            content.daSets.forEach {
                logger.d("restore, add daSet: ${it.title}")
                shortX.addOrUpdateDASet(it)
            }
            content.ruleSets.forEach {
                logger.d("restore, add ruleSets: ${it.title}")
                shortX.addOrUpdateRuleSet(it)
            }
            content.gestures.forEach {
                logger.d("restore, add gesture: ${it.label}")
                shortX.addGestureRecord(it)
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

            // Toggle
            val togglePb = File(tmpExtractedFile, TOGGLE_FILE_NAME)
            val toggleStore = BaseToggleSettingsStore(this, togglePb)
            toggleStore.read()
            val toggles = toggleStore.getAllToggles()

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

            // DA Sets
            val daSetsPb = File(tmpExtractedFile, DA_SET_FILE_NAME)
            val daSetsStore = BaseDirectActionSetsStore(this, daSetsPb)
            daSetsStore.read()
            val daSets = daSetsStore.getAllDirectActionSets()

            // Rule Sets
            val ruleSetsPb = File(tmpExtractedFile, RULE_SET_FILE_NAME)
            val ruleSetsStore = BaseRuleSetsStore(this, ruleSetsPb)
            ruleSetsStore.read()
            val ruleSets = ruleSetsStore.getAllRuleSets()

            // Gestures
            val gesturesPb = File(tmpExtractedFile, GESTURE_RECORD_FILE_NAME)
            val gesturesStore = BaseGestureRecordsStore(this, gesturesPb)
            gesturesStore.read()
            val gestures = gesturesStore.getAllGestureRecords()

            // Clean up.
            tmpExtractedFile.deleteRecursively()
            tmpZipFile.deleteRecursively()

            BackupPackageContent(
                das = das,
                rules = rules,
                toggles = toggles,
                pkgSets = pkgSets,
                gvs = gvs,
                daSets = daSets,
                ruleSets = ruleSets,
                gestures = gestures
            )
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
    val toggles: List<Toggle>,
    val pkgSets: List<PkgSet>,
    val gvs: List<GlobalVar>,
    val daSets: List<DirectActionSet>,
    val ruleSets: List<RuleSet>,
    val gestures: List<GestureRecord>,
)