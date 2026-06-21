package tornaco.apps.shortx.ui.addrule.fact

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.proto.common.CustomContextMenu
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.BrowserInterceptSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberBrowserInterceptSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.GVSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.MethodHookSelectorActivity
import tornaco.apps.shortx.ui.addrule.fact.selector.OnStartDynamicShortcutData
import tornaco.apps.shortx.ui.addrule.fact.selector.OnStartDynamicShortcutDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberGVSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberOnStartDynamicShortcutState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.contextmenu.CustomContextMenuSettingsActivity
import tornaco.apps.shortx.ui.settings.TileSettingsActivity
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun miscFactSelectHandler(addFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
    val context = LocalContext.current
    val i18N = LocalI18N.current

    val qsTileSelectState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                val tileNum = it.data?.getIntExtra("tile", 0) ?: 0
                addFact(Fact.OnQSTileClick(tile = StatusBarTile.forNumber(tileNum)))
            }
        }
    )

    val dynamicShortcutSelectState = rememberOnStartDynamicShortcutState(
        title = LocalI18N.current[I18N_R.UI_SETTINGS_DYNAMIC_SHORTCUT],
        message = null,
        onSelected = {
            addFact(
                Fact.OnStartDynamicShortcut(
                    shortcut = it.shortcut,
                    apps = it.appList,
                    pkgSets = it.pkgSetList
                )
            )
        })
    OnStartDynamicShortcutDialog(state = dynamicShortcutSelectState)


    val customContextMenuSelectState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                val num = it.data?.getIntExtra("menu", 0) ?: 0
                addFact(
                    Fact.OnMenuActionTrigger(
                        menu = CustomContextMenu.forNumber(num),
                        text = ""
                    )
                )
            }
        }
    )

    val gvSelectorDialogState = rememberGVSelectorState(onSelected = { varName ->
        addFact(Fact.GlobalVarChanged(gvId = varName))
    })
    GVSelectorDialog(state = gvSelectorDialogState)

    val browserInterceptSelectorState = rememberBrowserInterceptSelectorState(onSelected = { fact ->
        addFact(fact)
    })
    BrowserInterceptSelectorDialog(state = browserInterceptSelectorState)

    fun createUrl(tag: String) = "shortx://trigger?tag=${tag}"

    val deepLinkCallState = rememberTextInputState(
        title = i18N[I18N_R.UI_FACT_DEEPLINK_CALL],
        message = {
            val uri = createUrl(it)
            i18N[I18N_R.UI_FACT_DEEPLINK_CALL_MESSAGE, mapOf("url" to uri)]
        },
        textToCopy = {
            val uri = createUrl(it)
            uri
        }
    ) {
        addFact(Fact.DeepLinkCall(deepLinkTag = it))
    }
    TextInputDialog(state = deepLinkCallState)

    val methodHookLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val fact = result.popupSelectorResult<Fact.MethodHook>()
            fact?.let { addFact(it) }
        }
    )

    return { source ->
        when (source.clazz) {
            Fact.AnyFact::class -> {
                addFact(Fact.AnyFact())
                true
            }

            Fact.BackNavStart::class -> {
                addFact(Fact.BackNavStart())
                true
            }

            Fact.BackNavDone::class -> {
                addFact(Fact.BackNavDone())
                true
            }

            Fact.OnQSTileClick::class -> {
                qsTileSelectState.launch(TileSettingsActivity.intent(context, true))
                true
            }

            Fact.OnStartDynamicShortcut::class -> {
                dynamicShortcutSelectState.show(
                    source.getFactAs<Fact.OnStartDynamicShortcut>()?.let {
                        OnStartDynamicShortcutData(
                            shortcut = it.shortcut,
                            appList = it.apps,
                            pkgSetList = it.pkgSets
                        )
                    }
                )
                true
            }

            Fact.OnMenuActionTrigger::class -> {
                customContextMenuSelectState.launch(
                    CustomContextMenuSettingsActivity.intent(
                        context,
                        true
                    )
                )
                true
            }

            Fact.GlobalVarChanged::class -> {
                gvSelectorDialogState.show(source.getFactAs<Fact.GlobalVarChanged>()?.gvId)
                true
            }

            Fact.DeepLinkCall::class -> {
                deepLinkCallState.show(source.getFactAs<Fact.DeepLinkCall>()?.deepLinkTag)
                true
            }

            Fact.IncomingShare::class -> {
                addFact(Fact.IncomingShare())
                true
            }

            Fact.BrowserIntercept::class -> {
                browserInterceptSelectorState.show(source.getFactAs<Fact.BrowserIntercept>())
                true
            }

            Fact.MethodHook::class -> {
                methodHookLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getFactAs<Fact.MethodHook>(),
                        MethodHookSelectorActivity::class.java
                    )
                )
                true
            }

            else -> false
        }
    }
}
