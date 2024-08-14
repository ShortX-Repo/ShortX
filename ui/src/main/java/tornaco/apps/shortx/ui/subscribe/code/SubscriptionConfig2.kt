package tornaco.apps.shortx.ui.subscribe.code

import tornaco.apps.shortx.core.annotations.DoNotStrip

@DoNotStrip
data class SubscriptionConfig2(
    val flavors: List<Flavor>,
    val email: String,
    val qq: String,
)

data class Flavor(
    val text: String,
    val description: String,
    val priceUSD: Float,
    val priceCNY: Float,
)
