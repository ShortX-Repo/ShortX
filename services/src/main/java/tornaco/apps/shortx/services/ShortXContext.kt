package tornaco.apps.shortx.services

import tornaco.apps.shortx.services.gesture.GestureService
import tornaco.apps.shortx.services.rule.RuleService

interface ShortXContext {
    val rule: RuleService
    val gesture: GestureService
    val watchDog: WatchDogService
    val appState: AppStateService
}

class ShortXContextImpl : ShortXContext {
    override val rule = RuleService(this)
    override val gesture = GestureService(this)
    override val watchDog = WatchDogService(this)
    override val appState = AppStateService(this)
}