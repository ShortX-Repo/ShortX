package tornaco.apps.shortx.ui.subscribe.code

import tornaco.apps.shortx.core.annotations.DoNotStrip

@DoNotStrip
data class SubscriptionConfig(
    val priceUSD: Float,
    val priceCNY: Float,
    val email: String,
    val qq: String,
    val extra1: String,
)
