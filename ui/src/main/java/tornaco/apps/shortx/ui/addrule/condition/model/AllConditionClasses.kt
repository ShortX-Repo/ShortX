package tornaco.apps.shortx.ui.addrule.condition.model

val allConditionClasses = listOf(
    Condition.CurrentForegroundApp::class,
    Condition.MVEL::class,
    Condition.TRUE::class,
    Condition.EvaluateGlobalVar::class,
    Condition.BatteryPercent::class,
    Condition.AppIsRunning::class,
)
