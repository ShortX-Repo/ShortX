package tornaco.apps.shortx.services.util.logcat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.services.util.logcat.terminal.TerminalProcess
import tornaco.apps.shortx.services.util.logcat.terminal.TerminalResult

open class DefaultTerminal {
    protected open val commandPrefix: Array<String>? = null

    private fun createProcess(commands: Array<out String>) = (commandPrefix ?: emptyArray())
        .plus(commands)
        .let { Runtime.getRuntime().exec(it) }

    suspend fun executeNow(vararg command: String) = withContext(Dispatchers.IO) {
        val process = createProcess(command)

        val output = async(Dispatchers.IO) {
            process.inputStream.readBytes().decodeToString()
        }
        val error = async(Dispatchers.IO) {
            process.errorStream.readBytes().decodeToString()
        }
        val exitCode = process.waitFor()

        TerminalResult(exitCode, output.await(), error.await())
    }

    fun execute(vararg command: String) = createProcess(command).run {
        TerminalProcess(
            inputStream,
            errorStream,
            outputStream,
            this::destroy
        )
    }
}