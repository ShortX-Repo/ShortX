package tornaco.apps.shortx.ui.addrule.action.model

import android.content.Intent
import android.os.Parcelable
import androidx.annotation.IntRange
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.action.BreakActionExecuteScope
import tornaco.apps.shortx.core.proto.action.ContextMenuAction
import tornaco.apps.shortx.core.proto.action.DialogMenuItem
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.proto.action.MediaPlaybackAction
import tornaco.apps.shortx.core.proto.action.NavType
import tornaco.apps.shortx.core.proto.action.NotificationButton
import tornaco.apps.shortx.core.proto.action.RingerMode
import tornaco.apps.shortx.core.proto.action.ScrollViewToLocation
import tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp
import tornaco.apps.shortx.core.proto.common.ActionOnError
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtra
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AudioSource
import tornaco.apps.shortx.core.proto.common.CustomContextDataKey
import tornaco.apps.shortx.core.proto.common.DownloadSubject
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.HttpRequestHeader
import tornaco.apps.shortx.core.proto.common.HttpRequestMethod
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.proto.common.ProcessName
import tornaco.apps.shortx.core.proto.common.QSTile
import tornaco.apps.shortx.core.proto.common.Ringtone
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.common.TextFieldProp
import tornaco.apps.shortx.core.proto.common.VolumeDirection
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.LocalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.ui.addrule.ContextData
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.dats
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction
import java.util.UUID

fun defaultNewActionId() = "A-" + UUID.randomUUID().toString()

sealed interface Action : Parcelable {
    val id: String
    val isEnabled: Boolean
    val actionOnError: ActionOnError
    val note: String

    val customContextDataKey: CustomContextDataKey
    val contextData: List<ContextData>

    fun clone(
        id: String,
        note: String,
        isEnabled: Boolean,
        actionOnError: ActionOnError,
        contextData: List<ContextData>
    ): Action

    @Parcelize
    data class TakeScreenshot(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),

        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ShellCommand(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = dats<ContextDataMapping.ShellCommand>(
            customContextDataKey
        ),
        override val note: String = "",
        val command: String,
        val singleShot: Boolean
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class InputText(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "", val text: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class PerformContextMenuAction(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val action: ContextMenuAction
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class Delay(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val time: String,
        val useAlarm: Boolean
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ShowToast(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "", val message: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ShowDanmu(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val text: String,
        val icon: String?
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class MVEL(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = dats<ContextDataMapping.ExecuteMVEL>(
            customContextDataKey
        ),
        override val note: String = "", val expression: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class RemoteMVEL(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = dats<ContextDataMapping.ExecuteMVEL>(
            customContextDataKey
        ),
        override val note: String = "",
        val action: RemoteExecuteMVELAction
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ExecuteJS(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = dats<ContextDataMapping.ExecuteJS>(
            customContextDataKey
        ),
        override val note: String = "",
        val expression: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ShowOverlay(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val autoCollapse: Boolean,
        val settings: List<OverlayButtonSetting>,
        val tag: String,
        val maxWidth: Int,
        val maxHeight: Int,
        val buttonMinWidth: Int,
        val backgroundColor: String,
        val backgroundAlpha: Float,
        val overlayPaddingV: Int,
        val overlayPaddingH: Int,
        val enableGlobalDrag: Boolean,
        val disableAutoEdge: Boolean,
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class HideAllOverlay(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class HideOverlay(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val tags: List<String>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class LaunchApp(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val app: App,
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class LaunchAppByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StopApp(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StopAppByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class StartActivity(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val componentNameAsString: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StartActivityIntent(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val userId: Int,
        val action: String?,
        val flags: Int,
        val pkgName: String?,
        val className: String?,
        val data: String?,
        val extras: List<AndroidIntentExtra>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StartActivityUrlSchema(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val userId: Int,
        val urlSchema: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StartActivityIntentUri(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val intentUri: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ExpandNotification(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class PostNotification(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val tag: String,
        val title: String,
        val message: String,
        val overrideAppName: String,
        val largeIcon: String,
        val smallIcon: String,
        val isImportant: Boolean,
        val vibrate: Boolean,
        val sound: Boolean,
        val buttons: List<NotificationButton>,
        val onClickActions: List<Action>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class RemoveNotification(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val n: Fact.Notification
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class ClickNotification(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val n: Fact.Notification
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class ClickNotificationActionButton(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val n: Fact.Notification,
        val button: NotificationButton
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StartAppProcess(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class StartAppProcessByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetAppInactive(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetAppInactiveByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableApp(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableAppByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableApp(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableAppByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SuspendApp(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SuspendAppByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class UnSuspendApp(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class UnSuspendAppByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableBT(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableWifi(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableSensorsOff(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableHotSpot(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableData(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableNFC(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableFlashLight(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableDND(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableLocation(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableDarkMode(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableBT(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableWifi(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableSensorsOff(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableHotSpot(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableData(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableNFC(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableFlashLight(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableDND(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableLocation(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableDarkMode(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = ""
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class Brk(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val scope: BreakActionExecuteScope
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class WhileLoop(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val conditions: List<Condition>,
        val actions: List<Action>,
        val delay: Long,
        val repeat: Int,
        val op: Op
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class ForEachPkgSet(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val actions: List<Action>,
        val pkgSet: PkgSet,
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ShowAlertDialog(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val title: String,
        val message: String,
        val cancelable: Boolean = true,
        val positiveText: String? = null,
        val negativeText: String? = null,
        val neutralText: String? = null,
        val positiveActions: List<Action> = emptyList(),
        val negativeActions: List<Action> = emptyList(),
        val neutralActions: List<Action> = emptyList(),
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ShowTextFieldDialog(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val title: String,
        val message: String,
        val cancelable: Boolean = true,
        val textFields: List<TextFieldProp> = emptyList(),
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ShowMenuDialog(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val title: String,
        val message: String,
        val cancelable: Boolean,
        val items: List<DialogMenuItem>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class WriteGV(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val valueAsString: String,
        val varName: String,
        val op: WriteGlobalVarOp
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class CreateGV(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val globalVar: GlobalVar
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DeleteGV(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val varName: String,
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class MapNav(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val latitude: String,
        val longitude: String,
        val poi: String,
        val mapApp: MapApp,
        val navType: NavType
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class MapQueryBus(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val mapApp: MapApp,
        val bus: String,
        val city: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class FromDA(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val daId: String,
        val title: String?,
        val description: String?
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class FindAndClickViewByText(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val text: String,
        val isRegex: Boolean,
        val timeout: Long = 3000L
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class FindAndClickViewById(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val viewId: String,
        val isRegex: Boolean,
        val timeout: Long = 3000L
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class FindAndClickMatchedView(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class InputTap(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val x: String,
        val y: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class InputSwipe(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val startX: String,
        val startY: String,
        val endX: String,
        val endY: String,

        val swipeTime: String = "500"
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class WaitForIdle(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class IfThenElse(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val ifConditions: List<Condition> = listOf(Condition.TRUE()),
        val ifConditionOp: Op = Op.All,
        val ifActions: List<Action> = emptyList(),
        val elseActions: List<Action> = emptyList(),
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SwitchCase(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val cases: List<Case>,
        val deft: Default,
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }

        @Parcelize
        data class Case(
            val conditions: List<Condition> = listOf(Condition.TRUE()),
            val conditionOp: Op = Op.All,
            val actions: List<Action> = emptyList(),
            val description: String = "",
            val isBreak: Boolean = false,
            val isDisabled: Boolean = false,
            val id: String = defaultNewCaseId(),
        ) : Parcelable

        @Parcelize
        data class Default(
            val actions: List<Action> = emptyList(),
        ) : Parcelable

        companion object {
            fun defaultNewCaseId() = "Case-" + UUID.randomUUID().toString()
        }
    }

    @Parcelize
    data class WaitUtilConditionMatch(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val conditions: List<Condition> = listOf(Condition.TRUE()),
        val op: Op = Op.All,
        val timeout: Long,

        val quitFacts: List<Fact> = emptyList(),
        val quitConditions: List<Condition> = emptyList(),
        val quitOp: Op = Op.All,
        val isQuitEnabled: Boolean
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class BiometricVerify(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val title: String,
        val subtitle: String,
        val allowActions: List<Action> = emptyList(),
        val denyActions: List<Action> = emptyList(),
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ReadClipboard(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = dats<ContextDataMapping.ReadClipboard>(
            customContextDataKey
        ),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class GetTextFromScreenNode(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = dats<ContextDataMapping.GetTextFromScreenNode>(
            customContextDataKey
        ),
        override val note: String = "",

        val nodeId: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class WriteClipboard(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val text: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class CreatePkgSet(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val label: String,
        val description: String? = null,
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetRingerMode(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val mode: RingerMode
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableAutoBrightness(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisableAutoBrightness(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetBrightness(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        @IntRange(0, 255)
        val value: Int
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StopService(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val services: List<AppComponent>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class StartService(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val isFG: Boolean,
        val userId: Int,
        val action: String?,
        val flags: Int,
        val pkgName: String?,
        val className: String?,
        val data: String?,
        val extras: List<AndroidIntentExtra>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class RemoveTasks(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class RemoveTasksByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class KillProcessByName(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val processList: List<ProcessName>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class RemoveNotificationForPackage(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class RemoveNotificationForPackageByPkg(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val stringPairs: List<StringPair>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class HttpRequest(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val url: String,
        val method: HttpRequestMethod,
        val headers: List<HttpRequestHeader>,
        val requestBody: ProtoAny,

        val adapter: HttpRequestAdapter,
        val withCookieJar: Boolean,
        val trustAllCerts: Boolean,
        val executeInAppProcess: Boolean,
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class InjectKeyCode(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val code: Int
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class InjectCombineKeyCode(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val codes: List<Int>
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SleepScreen(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class WakeupScreen(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StartLastApp(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StopCurrentApp(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class AdjustVolume(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val direction: VolumeDirection
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class SetVolume(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val type: Int,
        val index: Int
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class Vibrate(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val vib1: Int,
        val vib2: Int,
        val vib3: Int,
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class AudioRecording(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val src: AudioSource,
        val fileNamePrefix: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StopAudioRecording(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SwitchMobileDataSlot(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val slotId: Int
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class CreateLocalVar(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val localVar: LocalVar

    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class WriteLocalVar(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val valueAsString: String,
        val varName: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StartGestureRecording(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StopGestureRecording(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ToggleGestureRecording(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class InjectGestureRecording(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val gestureRecord: GestureRecord,
        val speed: Float,
        val showGesturePathView: Boolean
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableUniversalCopy(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ShowDrawBoard(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class ShowClipboardView(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class EnableViewIdViewer(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class TTS(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val text: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetWallpaper(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val url: String,
        val crop: Boolean
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetRuleEnabled(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val ruleId: String,
        val enableRule: Boolean,
        val ruleLabel: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetAPMModeEnabled(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val isEnableAPM: Boolean
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetScreenTimeout(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val timeout: Long
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetScreenRotate(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val degree: ScreenRotateDegree
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ScrollViewTo(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val location: ScrollViewToLocation
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ClickTile(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val tile: QSTile,
        val isLongClick: Boolean
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class MediaPlayback(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val action: MediaPlaybackAction
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class AppShortcut(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val intent: Intent,
        val label: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class ConnectWifi(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val ssid: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DisconnectCurrentWifi(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StayAwake(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val stay: OnOffToggle
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class Toggle5G(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val onOff: OnOffToggle,
        val slotId: Int
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class LockDeviceNow(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SetMasterSync(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val sync: OnOffToggle
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class PlayRingtone(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val ringtone: Ringtone
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class DownloadFile(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = dats<ContextDataMapping.DownloadFile>(
            customContextDataKey
        ),
        override val note: String = "",

        val subject: DownloadSubject
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class SendSMS(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        val slotId: Int,
        val message: String,
        val to: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }

    @Parcelize
    data class StopAllActions(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",

        ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }


    @Parcelize
    data class NoAction(
        override val id: String = defaultNewActionId(),
        override val isEnabled: Boolean = true,
        override val actionOnError: ActionOnError = defaultActionOnError,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val contextData: List<ContextData> = emptyList(),
        override val note: String = "",
        val icon: String
    ) : Action {
        override fun clone(
            id: String,
            note: String,
            isEnabled: Boolean,
            actionOnError: ActionOnError,
            contextData: List<ContextData>
        ): Action {
            return this.copy(
                id = id,
                note = note,
                isEnabled = isEnabled,
                actionOnError = actionOnError,
                contextData = contextData
            )
        }
    }
}

@Parcelize
sealed interface HttpRequestAdapter : Parcelable {
    @Parcelize
    data object Raw : HttpRequestAdapter

    @Parcelize
    data class BodyJsonMap(val expressions: List<String>) : HttpRequestAdapter

    @Parcelize
    data class HeaderBodyJsonMap(val expressions: List<String>) : HttpRequestAdapter
}

val defaultActionOnError = ActionOnError.Continue