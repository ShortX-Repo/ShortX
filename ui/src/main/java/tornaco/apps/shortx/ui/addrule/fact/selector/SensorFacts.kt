package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.fact.FactSelectSource
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.sensorFactSelectHandler
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun SensorFacts(keyword: String, subscribeState: LVLStateHolder.State, setFact: (Fact) -> Unit) {
    val i18N = LocalI18N.current
    val items = mutableListOf(
        Fact.LightSensor::class,
        Fact.ProximitySensor::class,
        Fact.ProximitySensorStateChanged::class,
        Fact.AccelerometerSensor::class,
        Fact.ShakeDevice::class,
        Fact.Sound::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForFactSelector(i18N, it),
            icon = imageVectorForFact(it),
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed && isForSubscribedOnly(it)
            },
            iconColor = colorForFact(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    if (items.isNotEmpty()) {
        FactCategoryTitle(FactCategory.Sensor)
    }

    val itemSelected = sensorFactSelectHandler(setFact)

    SelectorList(
        items = items,
        itemSelected = {
            itemSelected(FactSelectSource.FromClass(it))
        }
    )
}