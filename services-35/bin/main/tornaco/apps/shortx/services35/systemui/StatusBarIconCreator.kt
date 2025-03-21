package tornaco.apps.shortx.services35.systemui

import android.graphics.drawable.Icon
import android.os.UserHandle
import com.android.internal.statusbar.StatusBarIcon

object StatusBarIconCreator {
    fun createIcon(
        user: UserHandle,
        resPackage: String,
        icon: Icon,
        iconLevel: Int,
        number: Int,
        contentDescription: CharSequence,
    ): StatusBarIcon {
        return StatusBarIcon(
            user,
            resPackage,
            icon,
            iconLevel,
            number,
            contentDescription,
            StatusBarIcon.Type.SystemIcon
        )
    }
}