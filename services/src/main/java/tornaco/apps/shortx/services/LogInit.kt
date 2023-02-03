package tornaco.apps.shortx.services

import android.util.Log
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.ChangelessFileNameGenerator
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.data.Dirs
import util.StringStack
import java.io.File


object LogInit {
    private const val LOG_PREFIX = "ShortX"

    @Suppress("ObjectPropertyName")
    private var _logDebug = BuildProp.APP_IS_DEBUG
    val logDebug get() = _logDebug

    // Log buffer for store early log.
    private val INIT_LOG_BUFFER: StringStack = StringStack()

    init {
        logAdapter = { level: Int, tag: String, msg: String ->
            when (level) {
                Log.VERBOSE -> if (_logDebug) XLog.v("$tag $msg")
                Log.DEBUG -> if (_logDebug) XLog.d("$tag $msg")
                Log.INFO -> if (_logDebug) XLog.i("$tag $msg")
                Log.WARN -> XLog.w("$tag $msg")
                Log.ERROR -> XLog.e("$tag $msg")
            }
        }
    }

    fun setLogDebug(enable: Boolean) {
        this._logDebug = enable
    }

    fun initEarlyLogging() {
        val androidPrinter: Printer = object : AndroidPrinter() {
            override fun println(logLevel: Int, tag: String, msg: String) {
                super.println(logLevel, tag, msg)
                synchronized(INIT_LOG_BUFFER) {
                    // Push to stack.
                    INIT_LOG_BUFFER.pushString("$tag: $msg")
                }
            }
        }
        XLog.init(
            LogConfiguration.Builder().logLevel(LogLevel.ALL)
                .tag(LOG_PREFIX).build(),
            androidPrinter
        )
        Log.d("ShortX-Init-Logging", "Init xlog with xposed printer.")
    }

    fun initStartedLogging() = kotlin.runCatching {
        val androidPrinter: Printer = AndroidPrinter()
        Dirs.baseServerLogDir().mkdirs()
        val filePrinter: Printer = FilePrinter.Builder(
            File(Dirs.baseServerLogDir(), "log").absolutePath
        )
            .fileNameGenerator(ChangelessFileNameGenerator("shortx.log"))
            .cleanStrategy(FileLastModifiedCleanStrategy(60 * 1000 * 60 * 24 * 7 /* 7 Days */))
            .flattener(ClassicFlattener())
            .build()
        XLog.init(
            LogConfiguration.Builder()
                .logLevel(LogLevel.ALL)
                .tag(LOG_PREFIX)
                .build(),
            filePrinter,
            androidPrinter
        )
        synchronized(INIT_LOG_BUFFER) {
            // Flush init log buffer.
            val initLogFile = File(
                File(Dirs.baseServerLogDir(), "log"),
                "shortx-init.log"
            )
            INIT_LOG_BUFFER.forEach {
                initLogFile.writeText(it)
            }
        }
        Log.d("ShortX-Logging", "Init xlog with file and android printer.")
    }.onFailure {
        Log.e("ShortX-Logging", "Error initStartedLogging: ${Log.getStackTraceString(it)}")
    }
}