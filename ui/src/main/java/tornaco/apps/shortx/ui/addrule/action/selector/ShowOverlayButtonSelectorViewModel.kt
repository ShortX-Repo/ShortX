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
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.text.Collator
import java.util.*
import javax.inject.Inject

data class OverlayButtonSelectorState(
    val installedApps: List<App> = emptyList(),
    val items: List<OverlayButtonItem> = emptyList(),
    val autoCollapse: Boolean = false,
    val canSave: Boolean = false,
    val id: String = defaultNewActionId()
)

@Parcelize
data class ShowOverlayButtonSettings(
    val id: String,
    val autoCollapse: Boolean,
    val items: List<OverlayButtonItem>
) : Parcelable

@Parcelize
data class OverlayButtonItem(
    val icon: String = BuildProp.DEFAULT_OVERLAY_BUTTON_ICON,
    val label: String = "",
    val actions: List<Action> = emptyList()
) : Parcelable

fun OverlayButtonItem.toOverlayButtonSetting(): OverlayButtonSetting {
    return OverlayButtonSetting.newBuilder().setIcon(icon).setLabel(label)
        .addAllActions(actions.map { it.toProtoAction().pack_() }).build()
}

fun OverlayButtonSetting.toOverlayButtonItem(): OverlayButtonItem {
    return OverlayButtonItem(icon = icon, label = label, actions = actionsList.mapNotNull {
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

    fun initFrom(settings: ShowOverlayButtonSettings) {
        logger.d("initFrom: $settings")
        updateState {
            copy(id = settings.id, items = settings.items, autoCollapse = settings.autoCollapse)
        }
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
        logger.d("addAction: $index $action current state size: ${state.value.items.size}")
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

    fun updateLabel(index: Int, label: String) {
        updateState {
            copy(
                items = items.toMutableList().apply {
                    val current = get(index)
                    set(index, current.copy(label = label))
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
        logger.d("addButton after, items size: ${state.value.items.size}")
    }
}