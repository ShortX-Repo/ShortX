package tornaco.apps.shortx.services.util.logcat.terminal

data class TerminalResult(
    val exitCode: Int = 0,
    val output: String = "",
    val errorOutput: String = ""
) {
    val isSuccessful get() = exitCode == 0
}