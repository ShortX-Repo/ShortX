package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.fact.FactSelectSource
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.systemFactSelectHandler
import tornaco.apps.shortx.ui.addrule.fact.systemFactSelectHandler2
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly
import kotlin.reflect.KClass

@Composable
fun SystemFacts(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: FactCategory,
    setFact: (Fact) -> Unit
) {
    val i18N = LocalI18N.current
    val itemSelected1 = systemFactSelectHandler(setFact)
    val itemSelected2 = systemFactSelectHandler2(setFact)

    fun buildItems(classes: List<KClass<out Fact>>): List<SelectorItem<Fact>> {
        return classes.map {
            SelectorItem(
                type = it,
                labelAndDescription = labelAndDescriptionForFactSelector(i18N, it),
                icon = imageVectorForFact(it),
                isForSubscribedOnly = !subscribeState.isSubscribed && isForSubscribedOnly(it),
                iconColor = colorForFact(it)
            )
        }.filter {
            keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
        }
    }

    @Composable
    fun renderSection(category: FactCategory, classes: List<KClass<out Fact>>) {
        val isVisible = selectedFilter == FactCategory.All || selectedFilter == category
        if (!isVisible) return

        val items = buildItems(classes)
        if (items.isEmpty()) return

        FactCategoryTitle(category)
        SelectorList(
            items = items,
            itemSelected = {
                val src = FactSelectSource.FromClass(it)
                itemSelected1(src) || itemSelected2(src)
            }
        )
    }

    val inputFacts = listOf(
        Fact.KeyEvent::class,
        Fact.AdvancedKeyEvent::class,
        Fact.CombineKeyEvent::class,
        Fact.HeadsetPlug::class,
        Fact.EdgeGesture::class,
        Fact.CornerSwipeToCenter::class,
        Fact.ScreenGesture::class,
    )

    val screenTimeFacts = listOf(
        Fact.ScreenRotate::class,
        Fact.WindowRotationChange::class,
        Fact.ScreenOn::class,
        Fact.ScreenOnTime::class,
        Fact.ScreenOff::class,
        Fact.UserPresent::class,
        Fact.UserPresentAtTheFirstTime::class,
        Fact.SunRiseSet::class,
        Fact.Alarm::class,
        Fact.RandomInPeriod::class,
        Fact.FixedInPeriod::class,
    )

    val stateFacts = listOf(
        Fact.WifiStatusChanged::class,
        Fact.ConnectedWifiSignalLevelChanged::class,
        Fact.WifiConnectedTo::class,
        Fact.WifiDisconnectedFrom::class,
        Fact.BTStatusChanged::class,
        Fact.BTConnectedTo::class,
        Fact.BTDisconnectedFrom::class,
        Fact.MobileDataStatusChanged::class,
        Fact.LocationStatusChanged::class,
        Fact.NFCStatusChanged::class,
        Fact.NFCTagDiscover::class,
        Fact.DNDStatusChanged::class,
        Fact.HotSpotStatusChanged::class,
        Fact.FlashLightStatusChanged::class,
        Fact.DarkModeStatusChanged::class,
        Fact.APMStatusChanged::class,
        Fact.VPNConnected::class,
        Fact.VPNDisconnected::class,
    )

    val powerFacts = listOf(
        Fact.BatteryLevelChanged::class,
        Fact.BatteryTemperatureChanged::class,
        Fact.ChargerPlug::class,
        Fact.ChargerUnplug::class,
        Fact.UsbDeviceAttached::class,
        Fact.UsbDeviceDetached::class,
    )

    val systemFacts = listOf(
        Fact.SystemReady::class,
        Fact.Broadcast::class,
        Fact.MediaStoreInsert::class,
        Fact.MediaStoreDelete::class,
        Fact.OnStartOp::class,
        Fact.OnFinishOp::class,
        Fact.SystemSettingsChanged::class,
        Fact.CallStateChanged::class,
        Fact.ClipboardContentChanged::class,
        Fact.IMEVisibilityChange::class,
        Fact.Logcat::class,
    )

    renderSection(FactCategory.Input, inputFacts)
    renderSection(FactCategory.ScreenTime, screenTimeFacts)
    renderSection(FactCategory.State, stateFacts)
    renderSection(FactCategory.Power, powerFacts)
    renderSection(FactCategory.System, systemFacts)
}