package tornaco.apps.shortx.core.os

import android.os.UserHandle

val myUserId get() = UserHandle.myUserId()

fun Int.isMainUser() = this == UserHandle.USER_SYSTEM