package tornaco.apps.shortx.core.util

import android.util.AtomicFile
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Read from an [AtomicFile] and close everything safely when done.
 */
@Throws(IOException::class)
inline fun <R> AtomicFileCompat.read(block: (FileInputStream) -> R): R {
    return openRead().use(block)
}

/**
 * Write to an [AtomicFile] and close everything safely when done.
 */
@Throws(IOException::class)
// Renamed to writeInlined() to avoid conflict with the hidden AtomicFile.write() that isn't inline.
inline fun AtomicFileCompat.writeInlined(block: (FileOutputStream) -> Unit) {
    startWrite().use {
        try {
            block(it)
            finishWrite(it)
        } catch (t: Throwable) {
            failWrite(it)
            throw t
        }
    }
}