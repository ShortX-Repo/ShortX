package tornaco.apps.shortx.core.os

import android.os.UserHandle

const val USER_MASK_ANY = -10241

val myUserId get() = UserHandle.myUserId()

fun Int.isMainUser() = this == UserHandle.USER_SYSTEM
fun Int.isMaskForAnyUser() = this == USER_MASK_ANY