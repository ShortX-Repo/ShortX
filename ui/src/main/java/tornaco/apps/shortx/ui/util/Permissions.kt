package tornaco.apps.shortx.ui.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.core.util.OsUtils

object Permissions {
    fun Context.shouldCheckStoragePermissionBeforeWriteDownloads(): Boolean {
        return !OsUtils.isROrAbove() && ContextCompat.checkSelfPermission(
            this,
            ManifestCompat.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }
}