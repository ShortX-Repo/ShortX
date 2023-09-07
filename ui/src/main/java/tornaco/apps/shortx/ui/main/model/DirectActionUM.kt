package tornaco.apps.shortx.ui.main.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.proto.da.DirectAction

@Parcelize
data class DirectActionUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val createTime: String,
    val icon: String,
    val iconColor: ULong,
    val runningInsCount: Int,

    // For discover
    val directAction: DirectAction,
    val versionCode: Long,

    // For online.
    val author: String = "ShortX",
    val isOnlineItem: Boolean = false,
    val url: String? = null,
    val isInstalled: Boolean = false,
    val hasUpdate: Boolean = false,

    // For Set
    val isDirectActionSet: Boolean = false,
    val itemsCount: Int = 0
) : Parcelable