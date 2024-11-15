package tornaco.apps.shortx.ui.settings.dav

import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.core.util.Logger


class DavRepo {
    private val logger = Logger("DavRepo")

    fun listFiles(profile: WebDavProfile) {
        val sardine: Sardine = OkHttpSardine()
        sardine.setCredentials(profile.userName, profile.password)
        val resources = sardine.list(profile.url)
        resources.forEach {
            logger.logOnDebug(it.displayName)
        }
    }
}