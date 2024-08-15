package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.ActivityClient
import android.os.Bundle
import com.topjohnwu.superuser.internal.Utils
import tornaco.apps.shortx.core.BuildPropExt
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.bundleToString
import java.io.File

// Debug purpose.
object ActivityClientHooks {
    private val logger = Logger("ShortXHook.ActivityClientHooks")

    fun hook() =
        BuildPropExt.onDebugBuild {
            kotlin.runCatching {
                logger.d("start hook")
                // void activityStopped(IBinder token, Bundle state, PersistableBundle persistentState, CharSequence description)
                beforeMethod(
                    ActivityClient::class.java,
                    "activityStopped"
                ) { param ->
                    val args = param.args.joinToString { it?.toString().orEmpty() }
                    logger.d("activityStopped: $args")
                    val state = param.args[1] as Bundle
                    val string = state.bundleToString()
                    val file = File(Utils.getContext().externalCacheDir, "bundle_state.txt")
                    file.writeText(string)
                    logger.d("State bundle ----> $file <-----")
                }
            }.onFailure {
                logger.e(it, "hook")
            }
        }

    private fun splitStringIntoChunks(input: String, chunkSize: Int = 100): List<String> {
        val result = mutableListOf<String>()
        var index = 0

        while (index < input.length) {
            // 截取从当前索引开始的长度为 chunkSize 的子字符串
            val chunk = input.substring(index, kotlin.math.min(index + chunkSize, input.length))
            result.add(chunk)
            index += chunkSize
        }
        return result
    }
}