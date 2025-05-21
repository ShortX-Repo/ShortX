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

package tornaco.apps.shortx.services.os

import com.elvishew.xlog.XLog
import java.io.File
import java.nio.charset.Charset

object MemoryReader {
    private val memFile =
        File("/proc", "meminfo")

    private val regex = "\\s+".toRegex()

    fun read(): ProcMemoryInfo {
        var totalMemory: Long = 0
        var freeMemory: Long = 0
        var buffers: Long = 0

        var totalSwap: Long = 0
        var freeSwap: Long = 0
        var cached: Long = 0

        kotlin.runCatching {
            memFile.readLines(Charset.defaultCharset()).forEach { line ->
                val splits = line.split(regex).toTypedArray()
                when (splits[0]) {
                    "MemTotal:" -> totalMemory = (splits[1]).toLong() shl 10
                    "MemFree:" -> freeMemory = (splits[1]).toLong() shl 10
                    "Buffers:" -> buffers = (splits[1]).toLong() shl 10
                    "SwapTotal:" -> totalSwap = (splits[1]).toLong() shl 10
                    "SwapFree:" -> freeSwap = (splits[1]).toLong() shl 10
                    "Cached:", "Slab:" -> cached += (splits[1]).toLong() shl 10
                }
            }
        }.onFailure {
            XLog.e("MemoryReader read error", it)
        }

        return ProcMemoryInfo(totalSwap, freeSwap)
    }
}

data class ProcMemoryInfo(
    val totalSwap: Long,
    val freeSwap: Long
)