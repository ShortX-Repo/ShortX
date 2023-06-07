package tornaco.apps.shortx.ui.addrule.action.mapper

import android.content.ComponentName
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import tornaco.apps.shortx.ui.base.PREFIX_MD_ICON
import kotlin.reflect.KClass

fun labelAndDescriptionForActionSelector(
    i18N: I18N,
    action: KClass<out Action>
): Pair<String, String?> {
    return when (action) {
        Action.ShowToast::class -> {
            i18N["ui.action.show.toast"] to null
        }

        Action.ReadClipboard::class -> {
            i18N["ui.action.read.clipboard"] to null
        }

        Action.ShowDanmu::class -> {
            i18N["ui.action.show.danmu"] to null
        }

        Action.ShellCommand::class -> {
            i18N["ui.action.shell"] to null
        }

        Action.InputText::class -> {
            i18N["ui.action.input.text"] to null
        }

        Action.Delay::class -> {
            i18N["ui.action.delay"] to null
        }

        Action.TakeScreenshot::class -> {
            i18N["ui.action.take.screenshot"] to null
        }

        Action.MVEL::class -> {
            i18N["ui.title.mvel"] to null
        }

        Action.HideAllOverlay::class -> {
            i18N["ui.action.hide.all.overlay"] to null
        }

        Action.HideOverlay::class -> {
            i18N["ui.action.hide.overlay"] to null
        }

        Action.ShowOverlay::class -> {
            i18N["ui.action.show.overlay"] to null
        }

        Action.LaunchApp::class -> {
            i18N["ui.action.launch.app"] to null
        }

        Action.StartActivity::class -> {
            i18N["ui.action.start.activity"] to null
        }

        Action.StartActivityIntent::class -> {
            i18N["ui.action.start.activity.intent"] to null
        }

        Action.StartActivityUrlSchema::class -> {
            i18N["ui.action.start.activity.url.schema"] to null
        }

        Action.DisableBT::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.bt"])] to null
        Action.DisableDND::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dnd"])] to null
        Action.DisableLocation::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.gps"])] to null
        Action.DisableDarkMode::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dark.mode"])] to null
        Action.DisableData::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.data"])] to null
        Action.DisableFlashLight::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.flashlight"])] to null
        Action.DisableHotSpot::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.hotspot"])] to null
        Action.DisableNFC::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.nfc"])] to null
        Action.DisableWifi::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.wifi"])] to null

        Action.EnableBT::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.bt"])] to null
        Action.EnableDND::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dnd"])] to null
        Action.EnableLocation::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.gps"])] to null
        Action.EnableDarkMode::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dark.mode"])] to null
        Action.EnableData::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.data"])] to null
        Action.EnableFlashLight::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.flashlight"])] to null
        Action.EnableHotSpot::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.hotspot"])] to null
        Action.EnableNFC::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.nfc"])] to null
        Action.EnableWifi::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.wifi"])] to null

        Action.StopApp::class -> i18N["ui.action.stop.app"] to null
        Action.StartAppProcess::class -> i18N["ui.action.start.app.process"] to null
        Action.EnableApp::class -> i18N["ui.action.enable.app"] to null
        Action.DisableApp::class -> i18N["ui.action.disable.app"] to null
        Action.SuspendApp::class -> i18N["ui.action.suspend.app"] to null
        Action.UnSuspendApp::class -> i18N["ui.action.unsuspend.app"] to null

        Action.Brk::class -> i18N["ui.action.brk"] to null
        Action.ShowAlertDialog::class -> i18N["ui.action.show.dialog"] to null
        Action.ExpandNotification::class -> i18N["ui.action.expand.notification"] to null
        Action.WhileLoop::class -> i18N["ui.action.while.loop"] to null
        Action.WriteGV::class -> i18N["ui.action.write.global.var"] to null
        Action.CreateGV::class -> i18N["ui.action.create.global.var"] to null
        Action.DeleteGV::class -> i18N["ui.action.delete.global.var"] to null
        Action.MapNav::class -> i18N["ui.action.map.nav"] to null
        Action.PostNotification::class -> i18N["ui.action.post.notification"] to null
        Action.FromDA::class -> i18N["ui.action.from.da"] to null
        Action.FindAndClickViewByText::class -> i18N["ui.action.find.click.view.by.text"] to null
        Action.InputTap::class -> i18N["ui.action.input.tap"] to null
        Action.InputSwipe::class -> i18N["ui.action.input.swipe"] to null
        Action.WaitForIdle::class -> i18N["ui.action.wait.for.idle"] to i18N["ui.action.wait.for.idle.description"]
        Action.IfThenElse::class -> i18N["ui.action.if.then.else"] to null
        Action.CreatePkgSet::class -> i18N["ui.pkg.set.add.title"] to null
        Action.NoAction::class -> "Noop" to null

        else -> {
            "N/A" to null
        }
    }
}

@Composable
fun labelLinesForAction(action: Action): List<String> {
    val i18N = LocalI18N.current
    return labelLinesForAction(i18N, action)
}

fun labelLinesForAction(i18N: I18N, action: Action): List<String> {
    return when (action) {
        is Action.ShowToast -> {
            listOf(i18N["ui.action.show.toast"], action.message)
        }

        is Action.ReadClipboard -> {
            listOf(i18N["ui.action.read.clipboard"])
        }

        is Action.ShowDanmu -> {
            listOf(i18N["ui.action.show.danmu"], action.text)
        }

        is Action.ShellCommand -> {
            listOf(i18N["ui.action.shell"], action.command)
        }

        is Action.InputText -> {
            listOf(i18N["ui.action.input.text"], action.text)
        }

        is Action.Delay -> {
            listOf(i18N["ui.action.delay"], action.time.toString())
        }

        is Action.TakeScreenshot -> {
            listOf(i18N["ui.action.take.screenshot"])
        }

        is Action.MVEL -> {
            listOf(i18N["ui.title.mvel"], action.expression)
        }

        is Action.HideAllOverlay -> {
            listOf(i18N["ui.action.hide.all.overlay"])
        }

        is Action.HideOverlay -> {
            listOf(
                i18N["ui.action.hide.overlay"],
                (action.tags.joinToString())
            )
        }

        is Action.ShowOverlay -> {
            mutableListOf(i18N["ui.action.show.overlay"]).apply {
                add("${action.settings.size} buttons")
            }
        }

        is Action.LaunchApp -> listOf(
            i18N["ui.action.launch.app"],
            "${action.app.label}$PREFIX_APP_ICON${action.app.pkg.pkgName}"
        )

        is Action.DisableBT -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.bt"])])
        is Action.DisableDND -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dnd"])])
        is Action.DisableLocation -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.gps"])])
        is Action.DisableDarkMode -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dark.mode"])])
        is Action.DisableData -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.data"])])
        is Action.DisableFlashLight -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.flashlight"])])
        is Action.DisableHotSpot -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.hotspot"])])
        is Action.DisableNFC -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.nfc"])])
        is Action.DisableWifi -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.wifi"])])

        is Action.EnableBT -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.bt"])])
        is Action.EnableDND -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dnd"])])
        is Action.EnableLocation -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.gps"])])
        is Action.EnableDarkMode -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dark.mode"])])
        is Action.EnableData -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.data"])])
        is Action.EnableFlashLight -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.flashlight"])])
        is Action.EnableHotSpot -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.hotspot"])])
        is Action.EnableNFC -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.nfc"])])
        is Action.EnableWifi -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.wifi"])])


        is Action.StopApp -> mutableListOf(i18N["ui.action.stop.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.StartAppProcess -> mutableListOf(i18N["ui.action.start.app.process"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.EnableApp -> mutableListOf(i18N["ui.action.enable.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.DisableApp -> mutableListOf(i18N["ui.action.disable.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.SuspendApp -> mutableListOf(i18N["ui.action.suspend.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.UnSuspendApp -> mutableListOf(i18N["ui.action.unsuspend.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.Brk -> {
            listOf(i18N["ui.action.brk"])
        }

        is Action.ShowAlertDialog -> {
            listOf(
                i18N["ui.action.show.dialog"],
                action.title,
                action.message
            )
        }

        is Action.StartActivity -> {
            val name: ComponentName? = ComponentName.unflattenFromString(
                action.componentNameAsString
            )
            if (name == null) {
                listOf(
                    i18N["ui.action.start.activity"]
                )
            } else {
                listOf(
                    i18N["ui.action.start.activity"],
                    "${action.componentNameAsString}$PREFIX_APP_ICON${name.packageName}"
                )
            }

        }

        is Action.StartActivityIntent -> {
            val name: ComponentName? = if (action.pkgName != null && action.className != null) {
                ComponentName(action.pkgName, action.className)
            } else {
                null
            }
            listOfNotNull(
                i18N["ui.action.start.activity.intent"],
                action.action,
                name?.let { "${it.flattenToShortString()}$PREFIX_APP_ICON${name.packageName}" }
            )
        }

        is Action.StartActivityUrlSchema -> {
            listOf(
                i18N["ui.action.start.activity.url.schema"],
                action.urlSchema
            )
        }

        is Action.ExpandNotification -> {
            listOf(i18N["ui.action.expand.notification"])
        }

        is Action.RemoteMVEL -> {
            listOf(
                action.action.title,
                action.action.description
            )
        }

        is Action.WhileLoop -> {
            listOf(
                i18N["ui.action.while.loop"]
            )
        }

        is Action.WriteGV -> {
            listOf(
                i18N["ui.action.write.global.var"],
                action.varName,
                action.valueAsString
            )
        }

        is Action.CreateGV -> {
            listOfNotNull(
                i18N["ui.action.create.global.var"],
                action.globalVar.name,
                action.globalVar.type.toType()?.name
            )
        }

        is Action.DeleteGV -> {
            listOfNotNull(
                i18N["ui.action.delete.global.var"],
                action.varName,
            )
        }

        is Action.MapNav -> listOf(
            i18N["ui.action.map.nav"],
            when (action.mapApp) {
                MapApp.Gaode -> {
                    i18N["ui.action.map.gaode"]
                }

                MapApp.Baidu -> {
                    i18N["ui.action.map.baidu"]
                }

                else -> {
                    "N/A"
                }
            }
        )

        is Action.PostNotification -> {
            listOf(
                i18N["ui.action.post.notification"],
                action.tag,
                action.title,
                action.message
            )
        }

        is Action.FromDA -> {
            mutableListOf(
                i18N["ui.action.from.da"],
            ).apply {
                if (action.title != null) {
                    add(action.title)
                    if (!action.description.isNullOrBlank()) {
                        add(action.description)
                    }
                } else {
                    add(i18N["ui.action.from.da.not.found"] + "${PREFIX_MD_ICON}${Remix.System.alert_fill}")
                }
            }
        }

        is Action.FindAndClickViewByText -> {
            listOf(
                i18N["ui.action.find.click.view.by.text"],
                action.text
            )
        }

        is Action.InputTap -> {
            listOf(
                i18N["ui.action.input.tap"],
                "[${action.x}, ${action.y}]"
            )
        }

        is Action.InputSwipe -> {
            listOf(
                i18N["ui.action.input.swipe"],
                "[${action.startX}, ${action.startY}] -> [${action.endX}, ${action.endY}]"
            )
        }

        is Action.WaitForIdle -> listOf(
            i18N["ui.action.wait.for.idle"],
            i18N["ui.action.wait.for.idle.description"]
        )

        is Action.IfThenElse -> mutableListOf(
            i18N["ui.action.if.then.else"]
        ).apply {
            val condition = action.ifConditions.joinToString {
                labelForConditionSelector(i18N, it::class).joinToString()
            }

            val ifAction = action.ifActions.joinToString {
                labelAndDescriptionForActionSelector(i18N, it::class).first
            }

            val elseAction = action.elseActions.joinToString {
                labelAndDescriptionForActionSelector(i18N, it::class).first
            }

            add(i18N["ui.action.if"] + " \uD83C\uDFAF " + condition + " ")
            add(i18N["ui.action.then"] + " ➡️ " + ifAction + " ")
            add(i18N["ui.action.else"] + " ➡️ " + elseAction + " ")
        }

        is Action.NoAction -> {
            listOf("Noop")
        }

        is Action.CreatePkgSet -> {
            listOfNotNull(i18N["ui.pkg.set.add.title"], action.label, action.description)
        }
    }
}
