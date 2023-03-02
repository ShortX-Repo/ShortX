package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.action.OverlayButtonSetting
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.text.Collator
import java.util.*
import javax.inject.Inject

data class OverlayButtonSelectorState(
    val installedApps: List<App> = emptyList(),
    val initialData: ShowOverlayButtonSettings = ShowOverlayButtonSettings(false, emptyList()),
    val items: List<OverlayButtonItem> = initialData.items,
    val autoCollapse: Boolean = false,
    val canSave: Boolean = false
)

@Parcelize
data class ShowOverlayButtonSettings(
    val autoCollapse: Boolean,
    val items: List<OverlayButtonItem>
) : Parcelable

@Parcelize
data class OverlayButtonItem(
    val icon: String = BuildProp.DEFAULT_OVERLAY_BUTTON_ICON,
    val actions: List<Action> = emptyList()
) : Parcelable

fun OverlayButtonItem.toOverlayButtonSetting(): OverlayButtonSetting {
    return OverlayButtonSetting.newBuilder().setIcon(icon)
        .addAllActions(actions.map { it.toProtoAction().pack_() }).build()
}

fun OverlayButtonSetting.toOverlayButtonItem(): OverlayButtonItem {
    return OverlayButtonItem(icon = icon, actions = actionsList.mapNotNull {
        it.toAction(shortXManager = shortXManager)
    })
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowOverlayButtonSelectorViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<OverlayButtonSelectorState>(context) {

    override fun initState(): OverlayButtonSelectorState {
        return OverlayButtonSelectorState()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                shortXManager.getInstalledApps()
                    .sortedWith { o1, o2 ->
                        Collator.getInstance(Locale.CHINESE).compare(o1.label, o2.label)
                    }.sortedBy { it.category.ordinal }
            }
            updateState {
                copy(installedApps = apps)
            }
        }
    }

    fun addAction(index: Int, action: Action) {
        updateState {
            copy(
                items = items.toMutableList().apply {
                    val current = get(index)
                    set(
                        index,
                        current.copy(actions = current.actions.toMutableList().apply {
                            add(action)
                        })
                    )
                }
            )
        }
    }

    fun removeAction(index: Int, action: Action) {
        updateState {
            copy(
                items = items.toMutableList().apply {
                    val current = get(index)
                    set(
                        index,
                        current.copy(actions = current.actions.toMutableList().apply {
                            remove(action)
                        })
                    )
                }
            )
        }
    }

    fun updateIcon(index: Int, icon: String) {
        updateState {
            copy(
                items = items.toMutableList().apply {
                    val current = get(index)
                    set(index, current.copy(icon = icon))
                }
            )
        }
    }

    fun setAutoCollapse(autoCollapse: Boolean) {
        updateState {
            copy(
                autoCollapse = autoCollapse
            )
        }
    }

    fun addButton() {
        updateState {
            copy(
                items = items.toMutableList().apply {
                    add(OverlayButtonItem())
                }
            )
        }
    }
}