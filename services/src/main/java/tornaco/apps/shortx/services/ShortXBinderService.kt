package tornaco.apps.shortx.services

import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.util.Logger
import java.util.*


class ShortXBinderService : IShortX.Stub() {
    private val logger = Logger("ShortXBinderService")

    override fun version(): String {
        return UUID.randomUUID().toString()
    }
}
