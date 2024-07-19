package tornaco.apps.shortx.services.rule.actor.impl

import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.os.SHELL_ERROR_NO_SUCH_FILE_OR_DIR
import java.io.File

object CopyFile {
    private val logger = Logger("CopyFile")

    suspend fun copyFile(
        shortXContext: ShortXContext,
        srcFile: File,
        destFile: File,
        deleteSrcOnSuccess: Boolean = true
    ) {
        kotlin.runCatching {
            require(srcFile.copyTo(destFile, true).exists())
            if (deleteSrcOnSuccess) kotlin.runCatching { srcFile.deleteRecursively() }
            logger.d("copyFile success - kotlin IO")
        }.onFailure {
            copyFileByShellCommand(shortXContext, srcFile, destFile, deleteSrcOnSuccess)
            logger.d("copyFile success - Shell")
        }
    }

    suspend fun copyFileByShellCommand(
        shortXContext: ShortXContext,
        srcFile: File,
        destFile: File,
        deleteSrcOnSuccess: Boolean = true
    ) {
        requireNotNull(
            shortXContext.shell.executeCommand(
                "mkdir -p ${destFile.parentFile.absolutePath} && cp ${srcFile.absolutePath} ${destFile.absolutePath}",
                false
            )
        ) { "Copy file fail - Shell res is null" }
            .also {
                logger.d("Shell res: $it")
                require(it.code == 0) { "Copy file fail: $it" }
                require(!it.toString().contains(SHELL_ERROR_NO_SUCH_FILE_OR_DIR)) {
                    SHELL_ERROR_NO_SUCH_FILE_OR_DIR
                }
                if (deleteSrcOnSuccess) kotlin.runCatching { srcFile.deleteRecursively() }
                logger.d("Backup copy success.")
            }
    }
}