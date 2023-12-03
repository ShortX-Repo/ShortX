package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

@Composable
fun SystemConditions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    setCondition: (Condition) -> Unit
) {
    val i18N = LocalI18N.current
    val items = listOf(
        Condition.RequireScreenRotate::class,
        Condition.RequireWindowRotation::class,
        Condition.ScreenIsOn::class,
        Condition.VPNIsConnected::class,
        Condition.BatteryPercent::class,
        Condition.ChargeState::class,
        Condition.PlugState::class,
        Condition.KeyguardIsLocked::class,
        Condition.ScreenOrientationIsPort::class,
        Condition.TimeInRange::class,
        Condition.RequireWifiConnected::class,
        Condition.RequireWifiDisconnected::class,
        Condition.RequireBTConnected::class,
        Condition.RequireBTDisconnected::class,
        Condition.RequireMobileDataEnabled::class,
        Condition.IsInCall::class,
        Condition.IsRinging::class,
        Condition.IsHeadsetPlug::class,
        Condition.RequireSensorOff::class,
        Condition.RequireTileState::class,
        Condition.RequireAPMMode::class,
        Condition.RequireIMEVisibility::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelForConditionSelector(i18N, it)[0] to null,
            icon = iconForCondition(it),
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed && isForSubscribedOnly(it)
            },
            iconColor = colorForCondition(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    if (items.isNotEmpty()) {
        ConditionCategoryTitle(ConditionCategory.System)
    }

    val selectHandler: (ConditionSelectSource) -> Boolean = systemConditionSelectHandler {
        setCondition(it)
    }

    SelectorList(
        items = items,
        itemSelected = { it ->
            selectHandler(ConditionSelectSource.FromClass(it))
        }
    )
}