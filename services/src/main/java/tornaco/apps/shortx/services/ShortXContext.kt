package tornaco.apps.shortx.services

import tornaco.apps.shortx.services.app.AppStateService
import tornaco.apps.shortx.services.app.PkgSetService
import tornaco.apps.shortx.services.gesture.GestureService
import tornaco.apps.shortx.services.input.InputService
import tornaco.apps.shortx.services.os.UserService
import tornaco.apps.shortx.services.overlay.OverlayService
import tornaco.apps.shortx.services.rule.RuleService
import tornaco.apps.shortx.services.ui.UIService

interface ShortXContext {
    val rule: RuleService
    val gesture: GestureService
    val watchDog: WatchDogService
    val appState: AppStateService
    val pkgSet: PkgSetService
    val overlay: OverlayService
    val ui: UIService
    val input: InputService
    val user: UserService
}

class ShortXContextImpl : ShortXContext {
    override val rule = RuleService(this)
    override val gesture = GestureService(this)
    override val watchDog = WatchDogService(this)
    override val appState = AppStateService(this)
    override val pkgSet = PkgSetService(this)
    override val overlay = OverlayService(this)
    override val ui = UIService(this)
    override val input = InputService(this)
    override val user = UserService(this)
}