package tornaco.apps.shortx.ui.row

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.proto.feature.AppFeature
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXAlertDialog
import tornaco.apps.shortx.ui.row.util.isLVLChecked

const val FREE_USER_MAX_PKG_SET_COUNT = 3
const val FREE_USER_MAX_RULE_COUNT = 3
const val FREE_USER_MAX_DA_COUNT = 3

class FeatureAccessChecker(private val appFeatureManager: AppFeatureManager) : CommonDialogState() {
    fun canAccess(feature: AppFeature): Boolean = appFeatureManager.canAccess(feature)

    fun accessOrShowDialog(feature: AppFeature, access: () -> Unit) {
        if (canAccess(feature)) access() else show()
    }
}

@Composable
fun rememberFeatureAccessDialogState(
    appFeatureManager: AppFeatureManager
): FeatureAccessChecker {
    return remember {
        FeatureAccessChecker(appFeatureManager)
    }
}

@Composable
fun FeatureAccessDialog(state: FeatureAccessChecker) {
    if (state.isShowing) {
        ShortXAlertDialog(
            onDismissRequest = {
                state.dismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    state.dismiss()
                }) {
                    Text(text = LocalI18N.current["ui.subscription.intro.yes"])
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    state.dismiss()
                }) {
                    Text(text = LocalI18N.current["ui.subscription.intro.no"])
                }
            },
            title = {
                Text(LocalI18N.current["ui.subscription.intro.title"])
            },
            text = {
                Text(text = LocalI18N.current["ui.subscription.intro.message"])
            })
    }
}

@Composable
fun featureAccessCheckUi(): FeatureAccessChecker {
    val dialogState = rememberFeatureAccessDialogState(AppFeatureManager(LocalContext.current))
    FeatureAccessDialog(dialogState)
    return dialogState
}

class AppFeatureManager(private val context: Context) {

    fun canAccess(feature: AppFeature): Boolean {
        return when (feature) {
            AppFeature.AppFeature_AddPkgSet -> {
                return shortXManager.getAllPkgSets(false).size <= FREE_USER_MAX_PKG_SET_COUNT
            }
            AppFeature.AppFeature_DanmuSettings -> {
                isLVLChecked(context)
            }
            AppFeature.AppFeature_AddRule -> {
                return shortXManager.getRuleCount() <= FREE_USER_MAX_RULE_COUNT
            }
            AppFeature.AppFeature_AddDirectAction -> {
                return shortXManager.getDirectActionCount() <= FREE_USER_MAX_DA_COUNT
            }
            AppFeature.AppFeature_ShareClipboard -> isLVLChecked(context)
            AppFeature.AppFeature_ShareFile -> isLVLChecked(context)
            AppFeature.AppFeature_ShareIntent -> isLVLChecked(context)
            AppFeature.AppFeature_ImportClipboard -> isLVLChecked(context)
            AppFeature.AppFeature_ImportFile -> isLVLChecked(context)
            AppFeature.AppFeature_CreateShortcut -> isLVLChecked(context)
            else -> {
                isLVLChecked(context)
            }
        }
    }
}