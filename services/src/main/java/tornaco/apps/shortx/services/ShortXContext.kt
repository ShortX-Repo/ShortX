package tornaco.apps.shortx.services

import tornaco.apps.shortx.services.app.AppStateService
import tornaco.apps.shortx.services.app.PkgSetService
import tornaco.apps.shortx.services.gesture.GestureService
import tornaco.apps.shortx.services.overlay.OverlayService
import tornaco.apps.shortx.services.rule.RuleService

interface ShortXContext {
    val rule: RuleService
    val gesture: GestureService
    val watchDog: WatchDogService
    val appState: AppStateService
    val pkgSet: PkgSetService
    val overlay: OverlayService
}

class ShortXContextImpl : ShortXContext {
    override val rule = RuleService(this)
    override val gesture = GestureService(this)
    override val watchDog = WatchDogService(this)
    override val appState = AppStateService(this)
    override val pkgSet = PkgSetService(this)
    override val overlay = OverlayService(this)
}