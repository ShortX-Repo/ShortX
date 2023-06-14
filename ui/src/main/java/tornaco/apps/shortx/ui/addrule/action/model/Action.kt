package tornaco.apps.shortx.ui.addrule.action.model

import android.os.Parcelable
import androidx.annotation.IntRange
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.proto.action.RingerMode
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtra
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.ui.addrule.ContextData
import tornaco.apps.shortx.ui.addrule.actionContextData
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction
import java.util.UUID

fun defaultNewActionId() = "A-" + UUID.randomUUID().toString()

sealed interface Action : Parcelable {
    val id: String
    val note: String

    fun clone(id: String, note: String): Action

    @Parcelize
    data class TakeScreenshot(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class ShellCommand(
        override val id: String = defaultNewActionId(),
        override val note: String = "", val command: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class InputText(
        override val id: String = defaultNewActionId(),
        override val note: String = "", val text: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class Delay(
        override val id: String = defaultNewActionId(),
        override val note: String = "", val time: Long
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class ShowToast(
        override val id: String = defaultNewActionId(),
        override val note: String = "", val message: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class ShowDanmu(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val text: String,
        val icon: String?
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class MVEL(
        override val id: String = defaultNewActionId(),
        override val note: String = "", val expression: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class RemoteMVEL(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val action: RemoteExecuteMVELAction
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class ShowOverlay(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val autoCollapse: Boolean,
        val settings: List<OverlayButtonSetting>,
        val tag: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class HideAllOverlay(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class HideOverlay(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val tags: List<String>
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class LaunchApp(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val app: App,
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class StopApp(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }


    @Parcelize
    data class StartActivity(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val componentNameAsString: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class StartActivityIntent(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val userId: Int,
        val action: String?,
        val flags: Int,
        val pkgName: String?,
        val className: String?,
        val data: String?,
        val extras: List<AndroidIntentExtra>
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class StartActivityUrlSchema(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val userId: Int,
        val urlSchema: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class ExpandNotification(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class PostNotification(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val tag: String,
        val title: String,
        val message: String,
        val largeIcon: String,
        val smallIcon: String,
        val isImportant: Boolean,
        val vibrate: Boolean,
        val sound: Boolean,
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class StartAppProcess(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class SetAppInactive(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableApp(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableApp(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class SuspendApp(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class UnSuspendApp(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableBT(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableWifi(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableSensorsOff(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableHotSpot(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableData(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableNFC(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableFlashLight(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableDND(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableLocation(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableDarkMode(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableBT(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableWifi(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableSensorsOff(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableHotSpot(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableData(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableNFC(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableFlashLight(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableDND(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableLocation(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableDarkMode(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class Brk(
        override val id: String = defaultNewActionId(),
        override val note: String = ""
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class WhileLoop(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val conditions: List<Condition>,
        val actions: List<Action>,
        val delay: Long,
        val repeat: Int,
        val op: Op
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class ShowAlertDialog(
        override val id: String = defaultNewActionId(),
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
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class WriteGV(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val valueAsString: String,
        val varName: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class CreateGV(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val globalVar: GlobalVar
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DeleteGV(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val varName: String,
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class MapNav(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val latitude: String,
        val longitude: String,
        val poi: String,
        val mapApp: MapApp
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class FromDA(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val daId: String,
        val title: String?,
        val description: String?
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class FindAndClickViewByText(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val text: String,
        val isRegex: Boolean,
        val timeout: Long = 3000L
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class InputTap(
        override val id: String = defaultNewActionId(),
        override val note: String = "",

        val x: Int,
        val y: Int
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class InputSwipe(
        override val id: String = defaultNewActionId(),
        override val note: String = "",

        val startX: Int,
        val startY: Int,
        val endX: Int,
        val endY: Int,

        val swipeTime: Long = 500
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class WaitForIdle(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class IfThenElse(
        override val id: String = defaultNewActionId(),
        override val note: String = "",

        val ifConditions: List<Condition> = listOf(Condition.TRUE()),
        val ifConditionOp: Op = Op.All,
        val ifActions: List<Action> = emptyList(),
        val elseActions: List<Action> = emptyList(),
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class ReadClipboard(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class WriteClipboard(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val text: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class CreatePkgSet(
        override val id: String = defaultNewActionId(),
        override val note: String = "",

        val label: String,
        val description: String? = null,
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class SetRingerMode(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val mode: RingerMode
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class EnableAutoBrightness(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class DisableAutoBrightness(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }

    @Parcelize
    data class SetBrightness(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        @IntRange(0, 255)
        val value: Int
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }


    @Parcelize
    data class NoAction(
        override val id: String = defaultNewActionId(),
        override val note: String = "",
        val icon: String
    ) : Action {
        override fun clone(id: String, note: String): Action {
            return this.copy(id = id, note = note)
        }
    }
}

val Action.contextData: List<ContextData> get() = this::class.actionContextData()