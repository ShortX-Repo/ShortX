package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.mapper.colorForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.iconForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly
import kotlin.reflect.KClass

@Composable
fun SystemConditions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: ConditionCategory,
    setCondition: (Condition) -> Unit
) {
    val i18N = LocalI18N.current
    val selectHandler: (ConditionSelectSource) -> Boolean = systemConditionSelectHandler {
        setCondition(it)
    }

    fun buildItems(classes: List<KClass<out Condition>>): List<SelectorItem<Condition>> {
        return classes.map {
            SelectorItem(
                type = it,
                labelAndDescription = labelForConditionSelector(i18N, it)[0] to null,
                icon = iconForCondition(it),
                isForSubscribedOnly = !subscribeState.isSubscribed && isForSubscribedOnly(it),
                iconColor = colorForCondition(it)
            )
        }.filter {
            keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
        }
    }

    @Composable
    fun renderSection(category: ConditionCategory, classes: List<KClass<out Condition>>) {
        val isVisible = selectedFilter == ConditionCategory.All || selectedFilter == category
        if (!isVisible) return

        val items = buildItems(classes)
        if (items.isEmpty()) return

        ConditionCategoryTitle(category)
        SelectorList(
            items = items,
            itemSelected = { kClass ->
                selectHandler(ConditionSelectSource.FromClass(kClass))
            }
        )
    }

    val screenTimeConditions = listOf(
        Condition.RequireScreenRotate::class,
        Condition.RequireWindowRotation::class,
        Condition.ScreenIsOn::class,
        Condition.TimeInRange::class,
        Condition.EvaluateScreenOnTime::class,
    )

    val stateConditions = listOf(
        Condition.VPNIsConnected::class,
        Condition.RequireWifiConnected::class,
        Condition.RequireWifiDisconnected::class,
        Condition.ConnectedWifiSignal::class,
        Condition.RequireBTConnected::class,
        Condition.RequireBTDisconnected::class,
        Condition.RequireMobileDataEnabled::class,
        Condition.RequireAPMMode::class,
        Condition.RequireIMEVisibility::class,
        Condition.RequireNotificationPanelExpanded::class,
        Condition.RequireRingerMode::class,
    )

    val deviceConditions = listOf(
        Condition.BatteryPercent::class,
        Condition.ChargeState::class,
        Condition.PlugState::class,
        Condition.RequireBTDeviceFound::class,
        Condition.IsInCall::class,
        Condition.IsRinging::class,
        Condition.IsHeadsetPlug::class,
        Condition.RequireSensorOff::class,
        Condition.RequireTileState::class,
    )

    val systemConditions = listOf(
        Condition.KeyguardIsLocked::class,
        Condition.ScreenOrientationIsPort::class,
    )

    renderSection(ConditionCategory.ScreenTime, screenTimeConditions)
    renderSection(ConditionCategory.State, stateConditions)
    renderSection(ConditionCategory.Device, deviceConditions)
    renderSection(ConditionCategory.System, systemConditions)
}