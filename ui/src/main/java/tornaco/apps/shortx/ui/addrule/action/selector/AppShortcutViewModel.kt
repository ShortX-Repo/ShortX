package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.os.uidToUserId
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject


data class AppShortcutState(
    val createShortcutIntentItems: List<CreateShortcutIntentItem> = emptyList()
)

data class CreateShortcutIntentItem(
    val label: String,
    val pkg: AppPkg,
    val activity: ActivityInfo,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AppShortcutViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AppShortcutState>(
        context, {
            AppShortcutState()
        }
    ) {

    fun load() {
        val all = context.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_CREATE_SHORTCUT),
            PackageManager.MATCH_ALL
        )
        val items = all.filter { it.activityInfo != null }
            .mapNotNull {
                runCatching {
                    logger.d(it)
                    val label = it.loadLabel(context.packageManager) ?: it.activityInfo.packageName
                    val activity = it.activityInfo
                    val userId = uidToUserId(activity.applicationInfo.uid)
                    val appPkg =
                        AppPkg.newBuilder().setPkgName(activity.packageName).setUserId(userId)
                            .build()
                    CreateShortcutIntentItem(label.toString(), appPkg, activity)
                }.getOrElse {
                    logger.e(it, "load")
                    null
                }
            }

        updateState {
            copy(createShortcutIntentItems = items)
        }
    }
}