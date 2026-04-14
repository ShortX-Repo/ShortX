package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

data class GestureHintModel(
    val token: Long,
    val anchorX: Float,
    val anchorY: Float,
    val directionX: Float,
    val directionY: Float,
    val progress: Float,
    val state: GestureHintState,
    val showArrow: Boolean = true,
)