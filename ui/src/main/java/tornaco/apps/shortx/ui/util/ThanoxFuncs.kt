package tornaco.apps.shortx.ui.util

import android.content.Context
import android.content.Intent
import github.tornaco.android.thanos.core.app.ThanosManagerNative

object ThanoxFuncs {
    val isThanoxActivated: Boolean
        get() = run {
            kotlin.runCatching { ThanosManagerNative.isServiceInstalled() }.getOrDefault(false)
        }

    private const val CLEAR_PROCESS_ACTION = "thanox.a.running_process.viewer"
    private const val THANOX_PKG_NAME = "github.tornaco.android.thanos"
    private const val THANOX_PRO_PKG_NAME = "github.tornaco.android.thanos.pro"

    fun goToProcessManagement(context: Context) {
        if (isThanoxActivated) {
            kotlin.runCatching {
                context.startActivity(
                    Intent().apply {
                        action = CLEAR_PROCESS_ACTION
                        setPackage(THANOX_PRO_PKG_NAME)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            }.onFailure {
                kotlin.runCatching {
                    context.startActivity(
                        Intent().apply {
                            action = CLEAR_PROCESS_ACTION
                            setPackage(THANOX_PKG_NAME)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                }
            }

        }
    }
}