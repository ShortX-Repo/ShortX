/*
 * Copyright (C) 2020 The exTHmUI Open Source Project
 * Copyright (C) 2021 AOSP-Krypton Project
 * Copyright (C) 2022 Nameless-AOSP Project
 * Copyright (C) 2023 the risingOS android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tornaco.apps.shortx.services.ui.danmu

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class DanmakuServiceListener : NotificationListenerService() {

    private val postedNotifications = mutableMapOf<String, Long>()

    var danmakuServiceInterface: DanmakuServiceInterface? = null

    override fun onNotificationPosted(sbn: StatusBarNotification) {
//        if (!(danmakuServiceInterface?.danmakuNotificationMode
//                ?: false) || !sbn.isClearable || sbn.isOngoing || sbn.getIsContentSecure()
//        ) return

        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE)
            ?: extras.getString(Notification.EXTRA_TITLE_BIG)
        val text = extras.getString(Notification.EXTRA_TEXT)

        var danmakuText = ""
        if (title?.isNotBlank() == true) danmakuText += "[$title] "
        if (text?.isNotBlank() == true) danmakuText += text

        val time = sbn.notification.`when`
        if (danmakuText.isNotBlank() && !(postedNotifications[danmakuText] == time)) {
            danmakuServiceInterface?.showNotificationAsOverlay(danmakuText)
            insertPostedNotification(danmakuText, time)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    private fun insertPostedNotification(danmakuText: String, time: Long) {
        if (postedNotifications.size >= NOTIFICATIONS_MAX_CACHED) postedNotifications.clear()
        postedNotifications[danmakuText] = time
    }

    companion object {
        private const val NOTIFICATIONS_MAX_CACHED = 99
    }
}
