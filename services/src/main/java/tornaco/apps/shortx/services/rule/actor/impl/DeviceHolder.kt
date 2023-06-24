package tornaco.apps.shortx.services.rule.actor.impl

import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ctrl.Controller
import tornaco.apps.shortx.services.ctrl.Device
import tornaco.apps.shortx.services.ctrl.Options

val ctrl by lazy {
    val device = Device(Options())
    val ctrl = Controller(device)
    Logger().w("Init ctrl: $ctrl")
    ctrl
}