package tornaco.apps.shortx.core.util.curl

import java.io.File
import java.io.IOException

object FileUtils {
    @JvmStatic
    @Throws(IOException::class)
    fun readFileToString(file: File) = file.readText()
}