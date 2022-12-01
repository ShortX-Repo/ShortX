package tornaco.apps.shortx.core.util

import android.os.UserHandle

object UserHandleUtils {
    fun userIdToUserHandle(userId: Int): UserHandle {
        return UserHandle.of(userId)
    }
}