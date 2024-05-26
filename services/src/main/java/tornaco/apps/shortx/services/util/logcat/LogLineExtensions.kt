package tornaco.apps.shortx.services.util.logcat

import android.util.LruCache
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.UIDS_MAPPINGS

private val logRegex = "(.{14}) (.{5,}?) (.{1,5}) (.{1,5}) (.) (.+?): (.+)".toRegex()

// time, uid, pid, tid, level, tag, message
private val uidRegex = "u(.+?).*a(.+)".toRegex()

private val uidsCache = LruCache<String, String>(200)

fun LogLine(
    line: String,
) = LogLine(id = 0L, content = "", original = line)

fun LogLine(
    id: Long,
    line: String,
    shortXContext: ShortXContext
) = runCatching {
    logRegex.find(line.trim())?.run {
        val uid = groupValues[2].replace(" ", "")
        val integerUid = uid.toIntOrNull() ?: UIDS_MAPPINGS[uid] ?: uidRegex.find(uid)?.run {
            100_000 * groupValues[1].toInt() + 10_000 + groupValues[2].toInt()
        }

        val packageName: String? = uidsCache[uid] ?: integerUid?.let {
            runCatching {
                shortXContext.appState.uidToApp(it)?.pkg?.pkgName?.also { packageName ->
                    uidsCache.put(uid, packageName)
                }
            }.getOrNull()
        }

        val time = groupValues[1].replace(" ", "").run {
            indexOf(".").let {
                substring(0, it).toLong() * 1000 + substring(it + 1).toLong()
            }
        }

        LogLine(
            id = id,
            dateAndTime = time,
            uid = uid,
            pid = groupValues[3].replace(" ", ""),
            tid = groupValues[4].replace(" ", ""),
            packageName = packageName,
            level = mapLevel(groupValues[5]),
            tag = groupValues[6].trim(),
            content = groupValues[7],
            original = groupValues[0]
        )
    }
}.getOrNull()

private fun mapLevel(level: String) = LogLevel.entries.find {
    it.letter == level
} ?: throw RuntimeException("wtf is $level")