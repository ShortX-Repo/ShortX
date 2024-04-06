package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.os.isMainUser
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtra
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtraType
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import java.util.*
import javax.inject.Inject


data class StartServiceSelectorState(
    val id: String = defaultNewActionId(),
    val action: String = Intent.ACTION_VIEW,
    val pkgName: String? = null,
    val className: String? = null,
    val data: String? = null,
    val userId: Int = myUserId,
    val userLabel: String = "N/A",
    val extras: List<AndroidIntentExtra> = emptyList(),
    val isFG: Boolean = false,
    val app: App? = null
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class StartServiceSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<StartServiceSelectorState>(
        context, {
            StartServiceSelectorState()
        }
    ) {

    override fun resetState() {
        super.resetState()
        logger.d("resetState")
        initFrom(
            Action.StartService(
                userId = myUserId,
                action = Intent.ACTION_VIEW,
                pkgName = null,
                className = null,
                flags = Intent.FLAG_ACTIVITY_NEW_TASK,
                data = null,
                extras = emptyList(),
                isFG = false
            )
        )
    }

    fun initFrom(settings: Action.StartService) {
        logger.d("initFrom: $settings")
        updateState {
            copy(
                id = settings.id,
                action = settings.action ?: "",
                pkgName = settings.pkgName ?: "",
                className = settings.className ?: "",
                data = settings.data ?: "",
                userId = settings.userId,
                extras = settings.extras,
                isFG = settings.isFG,

                app = settings.pkgName?.let {
                    shortXManager.pkgToApp(
                        AppPkg.newBuilder()
                            .setUserId(settings.userId)
                            .setPkgName(it)
                            .build(),
                    )
                },
                userLabel = if (settings.userId.isMainUser()) {
                    i18N["ui.common.user.main"]
                } else {
                    shortXManager.getAllUsers().firstOrNull { it.userId == settings.userId }?.name
                        ?: "N/A"
                }
            )
        }
    }

    fun updateAction(action: String) {
        updateState {
            copy(action = action)
        }
    }

    fun updatePkgName(pkgName: String) {
        logger.d("updatePkgName: $pkgName")
        updateState {
            copy(
                pkgName = pkgName,
                app = shortXManager.pkgToApp(
                    AppPkg.newBuilder()
                        .setUserId(state.value.userId)
                        .setPkgName(pkgName)
                        .build()
                )
            )
        }
    }

    fun updateClassName(className: String) {
        updateState {
            copy(className = className)
        }
    }

    fun updateData(data: String) {
        updateState {
            copy(data = data)
        }
    }

    fun updateFG(fg: Boolean) {
        updateState {
            copy(isFG = fg)
        }
    }

    fun updateUserId(userId: Int) {
        updateState {
            copy(
                userId = userId,
                app = pkgName?.let {
                    shortXManager.pkgToApp(
                        AppPkg.newBuilder()
                            .setUserId(userId)
                            .setPkgName(it)
                            .build()
                    )
                },
                userLabel = if (userId.isMainUser()) {
                    i18N["ui.common.user.main"]
                } else {
                    shortXManager.getAllUsers().firstOrNull { it.userId == userId }?.name ?: "N/A"
                }
            )
        }
    }

    fun addExtra(extra: AndroidIntentExtra) {
        updateState {
            copy(
                extras = state.value.extras.toMutableList().apply {
                    add(extra)
                }
            )
        }
    }

    fun updateExtraKey(index: Int, key: String) {
        updateState {
            copy(
                extras = state.value.extras.toMutableList().apply {
                    val updated = AndroidIntentExtra.newBuilder(get(index)).setKey(key).build()
                    set(index, updated)
                }
            )
        }
    }

    fun updateExtraValue(index: Int, value: String) {
        updateState {
            copy(
                extras = state.value.extras.toMutableList().apply {
                    val updated = AndroidIntentExtra.newBuilder(get(index)).setValue(value).build()
                    set(index, updated)
                }
            )
        }
    }

    fun updateExtraType(index: Int, type: AndroidIntentExtraType) {
        updateState {
            copy(
                extras = state.value.extras.toMutableList().apply {
                    val updated = AndroidIntentExtra.newBuilder(get(index)).setType(type).build()
                    set(index, updated)
                }
            )
        }
    }

    fun removeExtra(extra: AndroidIntentExtra) {
        updateState {
            copy(
                extras = state.value.extras.toMutableList().apply {
                    remove(extra)
                }
            )
        }
    }

    fun newExtra() {
        addExtra(AndroidIntentExtra.newBuilder().setType(AndroidIntentExtraType.String).build())
    }
}