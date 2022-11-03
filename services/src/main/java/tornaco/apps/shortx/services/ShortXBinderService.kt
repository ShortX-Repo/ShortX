package tornaco.apps.shortx.services

import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.Version
import tornaco.apps.shortx.core.util.Logger

class ShortXBinderService : IShortX.Stub() {
    private val logger = Logger("ShortXBinderService")

    override fun version(): Version {
        return Version(
            name = BuildProp.APP_VERSION_NAME,
            code = BuildProp.APP_VERSION_CODE
        )
    }

    override fun fingerprint() = BuildProp.BUILD_FINGERPRINT
}
