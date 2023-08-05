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

import tornaco.apps.shortx.core.proto.common.SystemSettingsAccessRecord

fun SystemSettingsAccessRecord(
    url: String,
    value: String,
    timeMillis: Long
): SystemSettingsAccessRecord {
    return SystemSettingsAccessRecord.newBuilder().setUrl(url)
        .setValue(value).setTimeMillis(timeMillis).build()
}

class SystemSettingsAccessRecorder {
    companion object {
        private const val MAX_ENTRY_SIZE = 300
    }

    private val readRecords = mutableListOf<SystemSettingsAccessRecord>()
    private val writeRecords = mutableListOf<SystemSettingsAccessRecord>()

    fun onRead(url: String, value: String) {
        readRecords.add(
            SystemSettingsAccessRecord(
                url,
                value,
                System.currentTimeMillis()
            )
        )
        if (readRecords.size > MAX_ENTRY_SIZE) {
            readRecords.removeFirst()
        }
    }

    fun onWrite(url: String, value: String) {
        writeRecords.add(
            SystemSettingsAccessRecord(
                url,
                value,
                System.currentTimeMillis()
            )
        )
        if (writeRecords.size > MAX_ENTRY_SIZE) {
            writeRecords.removeFirst()
        }
    }

    fun getReadRecords(): List<SystemSettingsAccessRecord> {
        return readRecords.toList()
    }

    fun getWriteRecords(): List<SystemSettingsAccessRecord> {
        return writeRecords.toList()
    }

    fun clearReadRecords() {
        readRecords.clear()
    }

    fun clearWriteRecords() {
        writeRecords.clear()
    }
}