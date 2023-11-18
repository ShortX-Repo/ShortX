/*
 * (C) Copyright 2022 Thanox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package tornaco.apps.shortx.services.rule.actor.impl

import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.Logger
import com.elvishew.xlog.flattener.PatternFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.ChangelessFileNameGenerator
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.data.Dirs.httpDir
import java.io.File

object HttpRequestLogging {
    var isLogEnabled = false

    val logFile get() = File(File(Dirs.httpDir(), "log"), "request.log")

    private var androidPrinter: Printer = AndroidPrinter()

    private var filePrinter: Printer = FilePrinter.Builder(
        File(httpDir(), "log").absolutePath
    ).fileNameGenerator(ChangelessFileNameGenerator("request.log"))
        .cleanStrategy(FileLastModifiedCleanStrategy(60 * 1000 * 60 * 24 * 7 /* 7 Days */))
        .flattener(PatternFlattener("{HH:mm} {t}: {m}"))
        .build()

    private val logger = Logger.Builder()
        .logLevel(LogLevel.ALL)
        .tag("")
        .printers(androidPrinter, filePrinter)
        .build()

    @JvmStatic
    fun log(message: String) {
        if (isLogEnabled) logger.w(message)
    }

    @JvmStatic
    fun log(message: String, vararg args: Any?) {
        if (isLogEnabled) kotlin.runCatching { logger.w(String.format(message, args)) }
    }

    @JvmStatic
    fun log(message: String, error: Throwable) {
        if (isLogEnabled) logger.e(message, error)
    }

    @JvmStatic
    fun section(message: String) {
        repeat(3) {
            log(System.lineSeparator())
        }
        log("------ $message ------ ")
        repeat(3) {
            log(System.lineSeparator())
        }
    }

    fun clearLogs() {
        logFile.writeText("--------------beginning of HttpLog.${System.lineSeparator()}")
    }
}