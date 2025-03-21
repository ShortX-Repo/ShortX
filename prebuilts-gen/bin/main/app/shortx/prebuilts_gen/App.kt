package app.shortx.prebuilts_gen

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.backup.DA_FILE_NAME
import tornaco.apps.shortx.core.backup.META_FILE_NAME
import tornaco.apps.shortx.core.backup.RULE_FILE_NAME
import tornaco.apps.shortx.core.store.BaseDirectActionSettingsStore
import tornaco.apps.shortx.core.store.BaseRuleSettingsStore
import tornaco.apps.shortx.core.util.ZipUtils
import tornaco.apps.shortx.core.util.logAdapter
import java.io.File

fun main() {
    logAdapter = { _: Int, tag: String, msg: String ->
        println("[$tag] $msg")
    }

    runBlocking {
        exportDev()
        exportPrebuilt()
    }
}

suspend fun exportDev() {
    coroutineScope {
        val buildDir = File("build")
        val prebuiltDir = File(buildDir, "dev")
        BaseDirectActionSettingsStore(this, File(prebuiltDir, DA_FILE_NAME).also {
            println(it.absolutePath)
        }).apply {
            addDirectActions(das = DevDAs.all, write = false)
            write()
        }

        BaseRuleSettingsStore(
            this,
            File(prebuiltDir, RULE_FILE_NAME).also { println(it.absolutePath) })
            .apply {
                addRules(rules = DevRules.all, write = false)
                write()
            }

        val metaFile = File(prebuiltDir, META_FILE_NAME)
        metaFile.writeText("AUTO GEN")

        val destDir = File("../ui/src/main/assets/dev")
        val zipFile = File(destDir, "dev-all.zip")
        zipFile.deleteRecursively()
        ZipUtils.zip(prebuiltDir.absolutePath, destDir.absolutePath, zipFile.name)
    }
}

suspend fun exportPrebuilt() {
    coroutineScope {
        val buildDir = File("build")
        val prebuiltDir = File(buildDir, "prebuilt")
        BaseDirectActionSettingsStore(this, File(prebuiltDir, DA_FILE_NAME).also {
            println(it.absolutePath)
        }).apply {
            addDirectActions(das = PrebuiltDAs.all, write = false)
            write()
        }

        BaseRuleSettingsStore(
            this,
            File(prebuiltDir, RULE_FILE_NAME).also { println(it.absolutePath) })
            .apply {
                addRules(rules = PrebuiltRules.all, write = false)
                write()
            }

        val metaFile = File(prebuiltDir, META_FILE_NAME)
        metaFile.writeText("AUTO GEN")

        val destDir = File("../ui/src/main/assets/prebuilt")
        val zipFile = File(destDir, "prebuilt-all.zip")
        zipFile.deleteRecursively()
        ZipUtils.zip(prebuiltDir.absolutePath, destDir.absolutePath, zipFile.name)
    }
}