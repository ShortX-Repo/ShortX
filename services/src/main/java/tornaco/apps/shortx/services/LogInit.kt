package tornaco.apps.shortx.services

import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.ZipUtils
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.data.Dirs
import util.StringStack
import java.io.File
import java.io.FileOutputStream


object LogInit {
    private const val LOG_PREFIX = "${BuildProp.LOG_PREFIX}Core"

    private var _logDebug = BuildPropInternal.APP_IS_DEBUG
    val logDebug get() = _logDebug

    // Log buffer for store early log.
    private val INIT_LOG_BUFFER: StringStack = StringStack()

    init {
        Log.w("$LOG_PREFIX-LogInit", "Installing logAdapter")
        logAdapter = { level: Int, tag: String, msg: String ->
            when (level) {
                Log.VERBOSE -> XLog.v("$tag $msg")
                Log.DEBUG -> XLog.d("$tag $msg")
                Log.INFO -> XLog.i("$tag $msg")
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
                    INIT_LOG_BUFFER.pushString("${DateUtils.formatLongForMessageTime(System.currentTimeMillis())} $tag: $msg")
                }
            }
        }
        XLog.init(
            LogConfiguration.Builder().logLevel(LogLevel.ALL)
                .tag(LOG_PREFIX).build(),
            androidPrinter
        )
        Log.d("$LOG_PREFIX-LogInit", "Init xlog with xposed printer.")
    }

    fun initStartedLogging() = kotlin.runCatching {
        val androidPrinter: Printer = AndroidPrinter()
        Dirs.baseServerLogDir().mkdirs()
        val filePrinter: Printer = FilePrinter.Builder(
            File(Dirs.baseServerLogDir(), "log").absolutePath
        )
            .fileNameGenerator(DateFileNameGenerator())
            .flattener(ClassicFlattener())
            .cleanStrategy(FileLastModifiedCleanStrategy(60 * 1000 * 60 * 24 * 3 /* 3 Days */))
            .build()
        XLog.init(
            LogConfiguration.Builder()
                .logLevel(if (BuildPropInternal.APP_IS_DEBUG) LogLevel.ALL else LogLevel.INFO)
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
            initLogFile.parentFile?.mkdirs()

            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText("-------------------")
            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText("DEVICE: ${Build.DEVICE}")
            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText("MANUFACTURER: ${Build.MANUFACTURER}")
            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText("ID: ${Build.ID}")
            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText("BRAND: ${Build.BRAND}")
            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText("MODEL: ${Build.MODEL}")
            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText("SDK: ${Build.VERSION.SDK_INT}")
            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText("SHORTX: ${BuildPropInternal.APP_VERSION_NAME} ${BuildPropInternal.APP_VERSION_CODE}")
            initLogFile.appendText(System.lineSeparator())
            initLogFile.appendText("-------------------")

            INIT_LOG_BUFFER.forEach {
                initLogFile.appendText(it)
                initLogFile.appendText(System.lineSeparator())
            }
        }
        Log.d("$LOG_PREFIX-LogInit", "Init xlog with file and android printer.")
    }.onFailure {
        Log.e("$LOG_PREFIX-LogInit", "Error initStartedLogging: ${Log.getStackTraceString(it)}")
    }

    fun writeLogsTo(pfd: ParcelFileDescriptor) {
        val logger = Logger("LogExport")

        val logDir = Dirs.baseServerLogDir()
        val tmpDir = Dirs.baseServerTmpDir().apply { this.mkdirs() }
        val name = "ShortX-Log-${System.currentTimeMillis()}.zip"
        ZipUtils.zip(logDir.absolutePath, tmpDir.absolutePath, name)
        val zipFile = File(tmpDir, name)
        logger.d("zipFile: $zipFile")

        pfd.use {
            val output = FileOutputStream(pfd.fileDescriptor)
            output.use { out ->
                zipFile.inputStream().use {
                    it.copyTo(out)
                }
            }
        }

        tmpDir.deleteRecursively()

        logger.d("Write success")
    }
}