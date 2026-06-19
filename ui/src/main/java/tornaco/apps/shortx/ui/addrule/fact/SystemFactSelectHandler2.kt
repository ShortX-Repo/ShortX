package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import github.tornaco.android.thanos.core.pm.Pkg
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.common.CallState
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.fact.mapper.factLabelKey
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.AppOpSelectorData
import tornaco.apps.shortx.ui.addrule.fact.selector.AppOpSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.NFCTagSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.ScreenOnTimeSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.SystemSettingsUrlSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAppOpSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberNFCTagSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberScreenOnTimeSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberSystemSettingsUrlSelectorState
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RegexTextInputDialog
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberRegexTextInputState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.util.XposedScope
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun systemFactSelectHandler2(addFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
    val context = LocalContext.current

    val batteryTempSelectorState = rememberTextInputState(
        title = LocalI18N.current[I18N_R.UI_FACT_BATTERY_TEMP_CHANGED],
        message = LocalI18N.current[I18N_R.UI_FACT_BATTERY_TEMP_CHANGED_TEMP],
        inputValidator = {
            (it.isNotEmpty() && ((it.toFloatOrNull() ?: 0f) <= 100f)).select(
                InputValidateResult.OK,
                InputValidateResult.Error("")
            )
        },
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onSelected = { levelString ->
            val level = levelString.toFloatOrNull()
            level?.let {
                addFact(Fact.BatteryTemperatureChanged(temp = it))
            }
        }
    )
    TextInputDialog(state = batteryTempSelectorState)

    val broadcastSelectorState = rememberTextInputState(
        title = LocalI18N.current[I18N_R.UI_FACT_BROADCAST],
        message = LocalI18N.current[I18N_R.UI_COMMON_ACTION],
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onSelected = { actionString ->
            addFact(Fact.Broadcast(actions = listOf(actionString)))
        }
    )
    TextInputDialog(state = broadcastSelectorState)


    val mediaInsertSelectorState = rememberRegexTextInputState(
        title = LocalI18N.current[I18N_R.UI_FACT_MEDIA_INSERT],
        message = "${LocalI18N.current[I18N_R.UI_FACT_MEDIA_INSERT_SUMMARY, mapOf("mediaPkgName" to PackageNames.mediaProviderPackages.joinToString())]} \n\n ${LocalI18N.current[I18N_R.UI_FACT_MEDIA_INSERT_INPUT_PATH]}",
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onSelected = { pathString, options ->
            addFact(
                Fact.MediaStoreInsert(
                    filterPathRegex = pathString.fallbackOnEmpty(".*"),
                    matchOptions = options
                )
            )

            XposedScope.requestScope(Pkg.systemUserPkg("com.android.providers.media.module"))
            XposedScope.requestScope(Pkg.systemUserPkg("com.google.android.providers.media.module"))
            XposedScope.requestScope(Pkg.systemUserPkg("com.android.providers.media"))
        }
    )
    RegexTextInputDialog(state = mediaInsertSelectorState)

    val mediaDeleteSelectorState = rememberRegexTextInputState(
        title = LocalI18N.current[I18N_R.UI_FACT_MEDIA_DELETE],
        message = "${LocalI18N.current[I18N_R.UI_FACT_MEDIA_DELETE_SUMMARY, mapOf("mediaPkgName" to PackageNames.mediaProviderPackages.joinToString())]} \n\n ${LocalI18N.current[I18N_R.UI_FACT_MEDIA_DELETE_INPUT_PATH]}",
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onSelected = { pathString, options ->
            addFact(
                Fact.MediaStoreDelete(
                    filterPathRegex = pathString.fallbackOnEmpty(".*"),
                    matchOptions = options
                )
            )
        }
    )
    RegexTextInputDialog(state = mediaDeleteSelectorState)

    val usbDeviceAttachState = rememberRegexTextInputState(
        title = LocalI18N.current[I18N_R.UI_FACT_USB_DEVICE_ATTACHED],
        message = LocalI18N.current[I18N_R.UI_FACT_USB_DEVICE_NAME],
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onSelected = { pathString, options ->
            addFact(
                Fact.UsbDeviceAttached(
                    deviceName = pathString.fallbackOnEmpty(".*"),
                    deviceNameRegexMatchOptions = options
                )
            )
        }
    )
    RegexTextInputDialog(state = usbDeviceAttachState)

    val usbDeviceDetachState = rememberRegexTextInputState(
        title = LocalI18N.current[I18N_R.UI_FACT_USB_DEVICE_DETACHED],
        message = LocalI18N.current[I18N_R.UI_FACT_USB_DEVICE_NAME],
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onSelected = { pathString, options ->
            addFact(
                Fact.UsbDeviceDetached(
                    deviceName = pathString.fallbackOnEmpty(".*"),
                    deviceNameRegexMatchOptions = options
                )
            )
        }
    )
    RegexTextInputDialog(state = usbDeviceDetachState)

    val onStartOpSelectorState = rememberAppOpSelectorState(
        title = LocalI18N.current[I18N_R.UI_FACT_OP_START],
        message = null,
        onSelected = {
            addFact(Fact.OnStartOp(apps = it.appList, pkgSets = it.pkgSetList, codes = it.opCodes))
        })
    AppOpSelectorDialog(state = onStartOpSelectorState)

    val onFinishOpSelectorState = rememberAppOpSelectorState(
        title = LocalI18N.current[I18N_R.UI_FACT_OP_FINISH],
        message = null,
        onSelected = {
            addFact(Fact.OnFinishOp(apps = it.appList, pkgSets = it.pkgSetList, codes = it.opCodes))
        })
    AppOpSelectorDialog(state = onFinishOpSelectorState)

    val systemSettingsChangeDialogState = rememberSystemSettingsUrlSelectorState(onSelected = {
        addFact(it)
    })
    SystemSettingsUrlSelectorDialog(state = systemSettingsChangeDialogState)

    val callStateSelectState =
        rememberMenuDialogState<String>(
            title = LocalI18N.current[I18N_R.UI_FACT_CALL_STATE_CHANGED],
            menuItems =
                CallState.entries.filterNot { it == CallState.UNRECOGNIZED }.map {
                    MenuDialogItem(
                        it.name,
                        LocalI18N.current["ui.fact.call.state.${it.name.lowercase()}"],
                    )
                }, onItemSelected = { _, id ->
                addFact(Fact.CallStateChanged(callState = CallState.valueOf(id)))
            }
        )
    MenuDialog(state = callStateSelectState)

    val clipboardRegexDialogState = rememberRegexTextInputState(
        title = LocalI18N.current[I18N_R.UI_FACT_CLIPBOARD_CONTENT_CHANGED],
        message = LocalI18N.current[I18N_R.UI_FACT_CLIPBOARD_CONTENT_CHANGED_REGEX],
        inputValidator = {
            InputValidateResult.OK
        },
        onSelected = { text, options ->
            addFact(Fact.ClipboardContentChanged(content = text, options = options))
        }
    )
    RegexTextInputDialog(state = clipboardRegexDialogState)

    val screenRotateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_FACT_SCREEN_ROTATE],
        menuItems = ScreenRotateDegree.entries.filterNot { it == ScreenRotateDegree.UNRECOGNIZED }
            .map {
                MenuDialogItem(it.name, title = LocalI18N.current[it.factLabelKey()])
            },
        onItemSelected = { data, id ->
            val degree = ScreenRotateDegree.valueOf(id)
            addFact(Fact.ScreenRotate(degree = degree))
        }
    )
    MenuDialog(state = screenRotateSelectorState)

    val screenOnTimeState = rememberScreenOnTimeSelectorState {
        addFact(it)
    }
    ScreenOnTimeSelectorDialog(screenOnTimeState)

    val windowRotateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_FACT_WINDOW_ROTATE],
        menuItems = ScreenRotateDegree.entries.filterNot { it == ScreenRotateDegree.UNRECOGNIZED }
            .map {
                MenuDialogItem(it.name, title = LocalI18N.current[it.factLabelKey()])
            },
        onItemSelected = { data, id ->
            val degree = ScreenRotateDegree.valueOf(id)
            addFact(Fact.WindowRotationChange(degree = degree))
        }
    )
    MenuDialog(state = windowRotateSelectorState)


    val imeVisibilitySelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_FACT_IME_VISIBILITY_CHANGE],
        menuItems = listOf(
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current[I18N_R.UI_FACT_IME_VISIBILITY_SHOW],
            ),
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current[I18N_R.UI_FACT_IME_VISIBILITY_HIDE],
            ),
        ),
        onItemSelected = { data, id ->
            val isShow = id.toBoolean()
            addFact(Fact.IMEVisibilityChange(isShown = isShow))
        }
    )
    MenuDialog(state = imeVisibilitySelectorState)

    val connectedWifiSignalState = rememberTextInputState(
        title = LocalI18N.current[I18N_R.UI_FACT_WIFI_SIGNAL_CHANGED],
        message = LocalI18N.current[I18N_R.UI_FACT_WIFI_SIGNAL_CHANGED_MESSAGE, mapOf(
            "max" to shortXManager.getMaxSignalLevel().toString()
        )]
    ) {
        addFact(Fact.ConnectedWifiSignalLevelChanged(level = it.toIntOrNull() ?: 0, rssi = 0))
    }
    TextInputDialog(state = connectedWifiSignalState)

    val nfcTagSelectorDialogState = rememberNFCTagSelectorState {
        addFact(Fact.NFCTagDiscover(uid = it))
    }
    NFCTagSelectorDialog(state = nfcTagSelectorDialogState)

    val logcatRegexDialogState = rememberRegexTextInputState(
        title = LocalI18N.current[I18N_R.UI_FACT_LOGCAT],
        message = null
    ) { str, op ->
        addFact(Fact.Logcat(regex = str, regexMatchOptions = op))
    }
    RegexTextInputDialog(state = logcatRegexDialogState)

    return { source ->
        when (source.clazz) {
            Fact.ScreenOff::class -> {
                addFact(Fact.ScreenOff())
                true
            }

            Fact.ScreenOn::class -> {
                addFact(Fact.ScreenOn())
                true
            }

            Fact.ScreenOnTime::class -> {
                screenOnTimeState.show(source.getFactAs())
                true
            }

            Fact.UserPresent::class -> {
                addFact(Fact.UserPresent())
                true
            }

            Fact.UserPresentAtTheFirstTime::class -> {
                addFact(Fact.UserPresentAtTheFirstTime())
                true
            }

            Fact.SystemReady::class -> {
                addFact(Fact.SystemReady())
                true
            }

            Fact.ConnectedWifiSignalLevelChanged::class -> {
                connectedWifiSignalState.show(source.getFactAs<Fact.ConnectedWifiSignalLevelChanged>()?.level?.toString())
                true
            }

            Fact.BatteryTemperatureChanged::class -> {
                batteryTempSelectorState.show(source.getFactAs<Fact.BatteryTemperatureChanged>()?.temp?.toString())
                true
            }

            Fact.ChargerPlug::class -> {
                addFact(Fact.ChargerPlug())
                true
            }

            Fact.ChargerUnplug::class -> {
                addFact(Fact.ChargerUnplug())
                true
            }

            Fact.Broadcast::class -> {
                broadcastSelectorState.show(source.getFactAs<Fact.Broadcast>()?.actions?.let {
                    if (it.isEmpty()) null else it[0]
                })
                true
            }

            Fact.VPNConnected::class -> {
                addFact(Fact.VPNConnected())
                true
            }

            Fact.VPNDisconnected::class -> {
                addFact(Fact.VPNDisconnected())
                true
            }

            Fact.MediaStoreInsert::class -> {
                mediaInsertSelectorState.show(
                    initialValue = source.getFactAs<Fact.MediaStoreInsert>()?.filterPathRegex,
                    options = source.getFactAs<Fact.MediaStoreInsert>()?.matchOptions
                )
                true
            }

            Fact.MediaStoreDelete::class -> {
                mediaDeleteSelectorState.show(
                    initialValue = source.getFactAs<Fact.MediaStoreDelete>()?.filterPathRegex,
                    options = source.getFactAs<Fact.MediaStoreDelete>()?.matchOptions
                )
                true
            }

            Fact.OnStartOp::class -> {
                onStartOpSelectorState.show(
                    AppOpSelectorData(
                        source.getFactAs<Fact.OnStartOp>()?.apps ?: emptyList(),
                        source.getFactAs<Fact.OnStartOp>()?.pkgSets ?: emptyList(),
                        source.getFactAs<Fact.OnStartOp>()?.codes ?: emptyList(),
                    )
                )
                true
            }

            Fact.OnFinishOp::class -> {
                onFinishOpSelectorState.show(
                    AppOpSelectorData(
                        source.getFactAs<Fact.OnFinishOp>()?.apps ?: emptyList(),
                        source.getFactAs<Fact.OnFinishOp>()?.pkgSets ?: emptyList(),
                        source.getFactAs<Fact.OnFinishOp>()?.codes ?: emptyList(),
                    )
                )
                true
            }

            Fact.SystemSettingsChanged::class -> {
                systemSettingsChangeDialogState.show(
                    source.getFactAs()
                )
                true
            }

            Fact.CallStateChanged::class -> {
                callStateSelectState.show()
                true
            }

            Fact.ClipboardContentChanged::class -> {
                clipboardRegexDialogState.show(
                    initialValue = source.getFactAs<Fact.ClipboardContentChanged>()?.content ?: "",
                    options = source.getFactAs<Fact.ClipboardContentChanged>()?.options
                )
                true
            }

            Fact.ScreenRotate::class -> {
                screenRotateSelectorState.show()
                true
            }

            Fact.WindowRotationChange::class -> {
                windowRotateSelectorState.show()
                true
            }

            Fact.IMEVisibilityChange::class -> {
                imeVisibilitySelectorState.show()
                true
            }

            Fact.NFCTagDiscover::class -> {
                nfcTagSelectorDialogState.show()
                true
            }

            Fact.Logcat::class -> {
                logcatRegexDialogState.show(
                    initialValue = source.getFactAs<Fact.Logcat>()?.regex ?: "",
                    options = source.getFactAs<Fact.Logcat>()?.regexMatchOptions
                )
                true
            }

            Fact.UsbDeviceAttached::class -> {
                usbDeviceAttachState.show(
                    initialValue = source.getFactAs<Fact.UsbDeviceAttached>()?.deviceName ?: "",
                    options = source.getFactAs<Fact.UsbDeviceAttached>()?.deviceNameRegexMatchOptions
                )
                true
            }

            Fact.UsbDeviceDetached::class -> {
                usbDeviceDetachState.show(
                    initialValue = source.getFactAs<Fact.UsbDeviceDetached>()?.deviceName ?: "",
                    options = source.getFactAs<Fact.UsbDeviceDetached>()?.deviceNameRegexMatchOptions
                )
                true
            }

            Fact.CpuAvgAvailability::class -> {
                addFact(Fact.CpuAvgAvailability())
                true
            }

            else -> false
        }
    }
}
