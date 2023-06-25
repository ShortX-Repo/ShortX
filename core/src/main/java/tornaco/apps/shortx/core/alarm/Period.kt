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

package tornaco.apps.shortx.core.alarm

import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import tornaco.apps.shortx.core.util.Logger
import java.util.Date

private val logger = Logger("RandomInPeriodExt")

fun RandomInPeriod.triggerDate(): Date {
    val start = android.icu.util.Calendar.getInstance().apply {
        time = Date()
        set(android.icu.util.Calendar.HOUR_OF_DAY, start.hour)
        set(android.icu.util.Calendar.MINUTE, start.minutes)
        set(android.icu.util.Calendar.SECOND, start.seconds)
    }.time

    val end = android.icu.util.Calendar.getInstance().apply {
        time = Date()
        set(android.icu.util.Calendar.HOUR_OF_DAY, end.hour)
        set(android.icu.util.Calendar.MINUTE, end.minutes)
        set(android.icu.util.Calendar.SECOND, end.seconds)
    }.time

    val randomTime = (start.time..end.time).random()
    return Date(randomTime).also {
        logger.d("triggerDateToday, start: $start end: $end rand: $it")
    }
}