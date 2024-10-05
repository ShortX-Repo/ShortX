package tornaco.apps.shortx.services.rule.actor.impl

import tornaco.apps.shortx.services.ctrl.Device
import tornaco.apps.shortx.services.ctrl.Options

val device by lazy {
    Device(Options())
}