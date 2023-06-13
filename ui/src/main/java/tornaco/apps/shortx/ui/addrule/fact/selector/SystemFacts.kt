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
import tornaco.apps.shortx.ui.base.LocalI18N

@Composable
fun SystemFacts(setFact: (Fact) -> Unit) {
    val i18N = LocalI18N.current
    val items = listOf(
        Fact.KeyEvent::class,
        Fact.EdgeGesture::class,

        Fact.ScreenOn::class,
        Fact.ScreenOff::class,
        Fact.UserPresent::class,
        Fact.SystemReady::class,

        Fact.Alarm::class,

        Fact.WifiStatusChanged::class,
        Fact.BTStatusChanged::class,
        Fact.MobileDataStatusChanged::class,
        Fact.LocationStatusChanged::class,
        Fact.NFCStatusChanged::class,
        Fact.DNDStatusChanged::class,
        Fact.HotSpotStatusChanged::class,
        Fact.FlashLightStatusChanged::class,
        Fact.DarkModeStatusChanged::class,

        Fact.BatteryLevelChanged::class,
        Fact.ChargerPlug::class,
        Fact.ChargerUnplug::class,

        Fact.Broadcast::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForFactSelector(i18N, it),
            icon = imageVectorForFact(it),
            iconColor = colorForFact(it)
        )
    }

    val itemSelected = systemFactSelectHandler(setFact)

    SelectorList(
        items = items,
        itemSelected = { it ->
            itemSelected(FactSelectSource.FromClass(it))
        }
    )
}