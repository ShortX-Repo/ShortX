package tornaco.apps.shortx.ui.dav

import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import tornaco.apps.shortx.core.util.Logger


class DavRepo {
    private val logger = Logger("DavRepo")
    private val pref = DavPref()

    fun listFiles() {
        val sardine: Sardine = OkHttpSardine()
        sardine.setCredentials(pref.getUserName(), pref.getPassword())
        val resources = sardine.list(pref.getServerUrl())
        resources.forEach {
            logger.p(it.displayName)
        }
    }
}