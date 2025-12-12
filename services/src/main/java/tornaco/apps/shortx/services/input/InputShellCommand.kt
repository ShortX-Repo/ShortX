package tornaco.apps.shortx.services.input

import android.os.Binder
import android.os.Build
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services28.input.InputShellCommandV28
import tornaco.apps.shortx.services29.input.InputShellCommandV29
import tornaco.apps.shortx.services30.input.InputShellCommandV30
import tornaco.apps.shortx.services31.input.InputShellCommandV31
import tornaco.apps.shortx.services33.input.InputShellCommandV33
import tornaco.apps.shortx.services34.input.InputShellCommandV34
import java.io.FileDescriptor
import java.io.FileOutputStream

@DoNotStrip
object InputShellCommand {
    private val logger = Logger("InputShell")

    private val fdIn = FileOutputStream(FileDescriptor.`in`).fd
    private val fdOut = FileOutputStream(FileDescriptor.out).fd
    private val fdErr = FileOutputStream(FileDescriptor.err).fd
    private val binder = Binder()

    fun onCommandOrThrow(cmd: String) {
        val args = cmd.split(" ").toTypedArray()
        logger.d("Args: ${args.joinToString()}")
        when (Build.VERSION.SDK_INT) {
            28 -> {
                logger.d("Input with InputShellCommandV28")
                InputShellCommandV28.main(args)
            }

            29 -> {
                logger.d("Input with InputShellCommandV29")
                InputShellCommandV29.main(args)
            }

            30 -> {
                logger.d("Input with InputShellCommandV30")
                InputShellCommandV30.main(args)
            }

            31, 32 -> {
                InputShellCommandV31().exec(binder, fdIn, fdOut, fdErr, args)
            }

            33 -> {
                InputShellCommandV33().exec(binder, fdIn, fdOut, fdErr, args)
            }

            34, 35, 36 -> {
                InputShellCommandV34().exec(binder, fdIn, fdOut, fdErr, args)
            }

            else -> {
                error("Not supported: android ${Build.VERSION.SDK_INT}")
            }
        }
    }
}