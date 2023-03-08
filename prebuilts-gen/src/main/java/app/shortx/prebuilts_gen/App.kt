package app.shortx.prebuilts_gen

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.backup.DA_FILE_NAME
import tornaco.apps.shortx.core.backup.META_FILE_NAME
import tornaco.apps.shortx.core.store.BaseDirectActionSettingsStore
import tornaco.apps.shortx.core.util.ZipUtils
import tornaco.apps.shortx.core.util.logAdapter
import java.io.File

fun main() {
    logAdapter = { _: Int, tag: String, msg: String ->
        println("[$tag] $msg")
    }

    runBlocking {
        exportDevDAs()
    }
}

suspend fun exportDevDAs() {
    coroutineScope {
        val buildDir = File("build")
        val prebuiltDir = File(buildDir, "prebuilts")
        BaseDirectActionSettingsStore(this, File(prebuiltDir, DA_FILE_NAME).also {
            println(it.absolutePath)
        }).apply {
            addDirectActions(das = DevDAs.all, write = false)
            write()
        }

        val metaFile = File(prebuiltDir, META_FILE_NAME)
        metaFile.writeText("AUTO GEN")

        val destDir = File("../ui/src/main/assets/prebuilts")
        val zipFile = File(destDir, "dev-all.zip")
        zipFile.deleteRecursively()
        ZipUtils.zip(prebuiltDir.absolutePath, destDir.absolutePath, zipFile.name)
    }
}