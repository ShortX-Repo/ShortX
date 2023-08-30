package tornaco.apps.shortx.ui.subscribe

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXAlertDialog
import kotlin.reflect.KClass

@Composable
fun FeatureAccessDialog(state: CommonDialogState) {
    if (state.isShowing) {
        val context = LocalContext.current
        ShortXAlertDialog(
            onDismissRequest = {
                state.dismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    state.dismiss()
                    SubscribeActivity.start(context)
                }) {
                    Text(text = LocalI18N.current["ui.subscription.intro.yes"])
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    state.dismiss()
                }) {
                    Text(text = LocalI18N.current["ui.subscription.intro.no"])
                }
            },
            title = {
                Text(LocalI18N.current["ui.subscription.intro.title"])
            },
            text = {
                Text(text = LocalI18N.current["ui.subscription.intro.message"])
            })
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
    Fact.AdvancedKeyEvent::class,
    Fact.FixedInPeriod::class,
    Fact.OnQSTileClick::class,
    Fact.HasFoundNodeOnScreen::class,
    // Remember to update also: tornaco.apps.shortx.services.rule.SubscribeOnlyModels.kt

    Condition.AppHasAudioFocus::class,
    Condition.RequireMobileDataEnabled::class,
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
    Action.FindAndClickViewById::class,
    Action.FindAndClickMatchedView::class,
    // Remember to update also: tornaco.apps.shortx.services.rule.SubscribeOnlyModels.kt
)


inline fun isForSubscribedOnly(clazz: KClass<*>, onRes: () -> Unit = {}): Boolean =
    subscribedOnlyModels().contains(clazz)