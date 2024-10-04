package tornaco.apps.shortx.services.input

import android.os.Binder
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services34.input.InputShellCommandV34
import java.io.FileDescriptor
import java.io.FileOutputStream

object InputShellCommand {
    private val logger = Logger("InputShell")

    private val fdIn = FileOutputStream(FileDescriptor.`in`).fd
    private val fdOut = FileOutputStream(FileDescriptor.out).fd
    private val fdErr = FileOutputStream(FileDescriptor.err).fd
    private val binder = Binder()

    fun onCommandOrThrow(cmd: String): Int {
        if (OsUtils.isUOrAbove()) {
            logger.d("Input with InputShellCommandV34")
            return InputShellCommandV34().exec(
                binder,
                fdIn,
                fdOut,
                fdErr,
                cmd.split(" ").toTypedArray()
            )
        } else {
            error("Not supported.")
        }
    }
}