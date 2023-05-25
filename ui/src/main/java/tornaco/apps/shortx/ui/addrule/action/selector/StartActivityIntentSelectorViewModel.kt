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
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject


data class StartActivityIntentSelectorState(
    val id: String = defaultNewActionId(),
    val action: String = Intent.ACTION_VIEW,
    val pkgName: String? = null,
    val className: String? = null,
    val data: String? = null,
    val userId: Int = myUserId,
    val userLabel: String = "N/A",
    val extras: List<AndroidIntentExtra> = emptyList(),
    val app: App? = null
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class StartActivityIntentSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<StartActivityIntentSelectorState>(
        context, StartActivityIntentSelectorState()
    ) {

    override fun resetState() {
        super.resetState()
        updateState(StartActivityIntentSelectorState())
    }

    fun initFrom(settings: Action.StartActivityIntent) {
        updateState {
            copy(id = settings.id)
        }
        updateAction(settings.action ?: "")
        updatePkgName(settings.pkgName ?: "")
        updateClassName(settings.className ?: "")
        updateData(settings.data ?: "")
        updateUserId(settings.userId)
        updateState { copy(extras = settings.extras) }
    }

    fun updateAction(action: String) {
        updateState {
            copy(action = action)
        }
    }

    fun updatePkgName(pkgName: String) {
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

    fun updateUserId(userId: Int) {
        updateState {
            copy(
                userId = userId,
                app = shortXManager.pkgToApp(
                    AppPkg.newBuilder()
                        .setUserId(userId)
                        .setPkgName(pkgName)
                        .build()
                ),
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