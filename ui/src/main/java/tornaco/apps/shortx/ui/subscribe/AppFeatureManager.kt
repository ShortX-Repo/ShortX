package tornaco.apps.shortx.ui.subscribe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXBottomSheet
import tornaco.apps.shortx.ui.base.ShortXBottomSheetState
import tornaco.apps.shortx.ui.composeLogger
import kotlin.reflect.KClass

@Composable
fun FeatureAccessDialog(state: ShortXBottomSheetState) {
    ShortXBottomSheet(state = state) {
        DisposableEffectWithLifeCycle(
            onResume = {
                composeLogger.d("FeatureAccessDialog onResume")
                if (LVLStateHolder.container.stateFlow.value.isSubscribed) {
                    state.dismiss()
                }
            },
            onPause = {
                composeLogger.d("FeatureAccessDialog onPause")
            }
        )
        SubscriptionIntro()
    }
}

@Composable
private fun SubscriptionIntro(
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(),
            painter = painterResource(
                id = tornaco.apps.shortx.ui.R.drawable.ui_no_access
            ),
            contentDescription = null,
        )

        MediumTitle(text = LocalI18N.current["ui.subscription.intro.message"])
        SectionSpacer()

        Button(modifier = Modifier
            .fillMaxWidth(0.68f),
            onClick = {
                SubscribeActivity.start(context)
            }) {
            Text(
                text = LocalI18N.current["ui.subscription.intro.yes"],
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


// Remember to update also: tornaco.apps.shortx.services.rule.SubscribeOnlyModels.kt
inline fun subscribedOnlyModels(onRes: () -> Unit = {}): Array<KClass<*>> = arrayOf(
    Fact.FixedInPeriod::class,
    Fact.VPNConnected::class,
    Fact.VPNDisconnected::class,
    Fact.BTConnectedTo::class,
    Fact.BTDisconnectedFrom::class,
    Fact.TaskRemoved::class,
    Fact.TaskRemovedAny::class,
    Fact.AdvancedKeyEvent::class,
    Fact.FixedInPeriod::class,
    Fact.OnQSTileClick::class,
    Fact.HasFoundNodeOnScreen::class,
    Fact.AppGainWindowFocus::class,
    Fact.AppLostWindowFocus::class,
    Fact.IMEVisibilityChange::class,
    Fact.ConnectedWifiSignalLevelChanged::class,
    Fact.NFCTagDiscover::class,
    Fact.ActivityStarted::class,
    Fact.ActivityStopped::class,
    Fact.ActivityDestroyed::class,
    Fact.AnyActivityStarted::class,
    Fact.AnyActivityStopped::class,
    Fact.AnyActivityDestroyed::class,
    Fact.UserPresentAtTheFirstTime::class,
    Fact.BatteryTemperatureChanged::class,
    Fact.MethodHook::class,
    Fact.PkgStartRunning::class,
    Fact.UsbDeviceDetached::class,
    Fact.UsbDeviceAttached::class,
    Fact.CombineKeyEvent::class,
    Fact.LightSensor::class,
    Fact.ProximitySensor::class,
    Fact.ProximitySensorStateChanged::class,
    Fact.ShakeDevice::class,
    // Remember to update also: tornaco.apps.shortx.services.rule.SubscribeOnlyModels.kt

    Condition.AppHasAudioFocus::class,
    Condition.RequireMobileDataEnabled::class,
    Condition.RequireFactTag::class,
    Condition.AppHasTask::class,
    Condition.AppHasTaskByPkg::class,
    Condition.AppHasWindowFocus::class,
    Condition.RequireIMEVisibility::class,
    Condition.ConnectedWifiSignal::class,
    Condition.TheXXTimeToday::class,
    Condition.RequireBTDeviceFound::class,
    Condition.RequireNotificationPanelExpanded::class,
    Condition.RequireWindowRotation::class,
    Condition.RequireScreenRotate::class,
    Condition.RequireWifiConnected::class,
    Condition.RequireWifiDisconnected::class,
    Condition.RequireBTConnected::class,
    Condition.RequireBTDisconnected::class,
    Condition.VPNIsConnected::class,
    Condition.RequireTileState::class,
    Condition.HasFoundPointsByColor::class,
    Condition.TextFromScreenNodeMatches::class,
    // Remember to update also: tornaco.apps.shortx.services.rule.SubscribeOnlyModels.kt

    Action.LaunchAppByPkg::class,
    Action.StopAppByPkg::class,
    Action.RemoveNotificationForPackageByPkg::class,
    Action.SuspendApp::class,
    Action.SuspendAppByPkg::class,
    Action.UnSuspendApp::class,
    Action.UnSuspendAppByPkg::class,
    Action.DisableApp::class,
    Action.DisableAppByPkg::class,
    Action.EnableApp::class,
    Action.EnableAppByPkg::class,
    Action.AudioRecording::class,
    Action.ShowOverlay::class,
    Action.SwitchMobileDataSlot::class,
    Action.HttpRequest::class,
    Action.InputText::class,
    Action.InputTap::class,
    Action.InputSwipe::class,
    Action.InjectGestureRecording::class,
    Action.ToggleGestureRecording::class,
    Action.StartGestureRecording::class,
    Action.StopGestureRecording::class,
    Action.EnableUniversalCopy::class,
    Action.TTS::class,
    Action.FindAndClickViewByText::class,
    Action.FindAndClickViewById::class,
    Action.FindAndClickMatchedView::class,
    Action.ClickTile::class,
    Action.BiometricVerify::class,
    Action.GetTextFromScreenNode::class,
    Action.ForEachPkgSet::class,
    Action.LockDeviceNow::class,
    Action.ConnectWifi::class,
    Action.ScrollViewTo::class,
    Action.ShowDrawBoard::class,
    Action.PerformContextMenuAction::class,
    Action.WaitUtilConditionMatch::class,
    Action.SetScreenTimeout::class,
    Action.ClickNotification::class,
    Action.ClickNotificationActionButton::class,
    Action.ShowClipboardView::class,
    Action.Toggle5G::class,
    Action.SendSMS::class,
    Action.SwitchCase::class,
    Action.ReplaceRegex::class,
    Action.MatchRegex::class,
    Action.DownloadFile::class,
    Action.InjectKeyCode::class,
    Action.InjectCombineKeyCode::class,
    Action.AreaScreenshot::class,
    Action.SelectScreenArea::class,
    Action.OcrDetect::class,
    Action.FindImagePosition::class,
    Action.FindPointsByColor::class,
    Action.LaunchPinedItem::class,
    Action.ShowFloatWindow::class,
    Action.ShowStatusBarClip::class,
    // Remember to update also: tornaco.apps.shortx.services.rule.SubscribeOnlyModels.kt
)


inline fun isForSubscribedOnly(clazz: KClass<*>, onRes: () -> Unit = {}): Boolean =
    subscribedOnlyModels().contains(clazz)