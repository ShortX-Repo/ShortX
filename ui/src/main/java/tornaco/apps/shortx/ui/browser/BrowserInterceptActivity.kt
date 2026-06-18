package tornaco.apps.shortx.ui.browser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import tornaco.apps.shortx.core.shortXManager

class BrowserInterceptActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_VIEW) {
            handleViewIntent(intent)
        }
        finish()
    }

    private fun handleViewIntent(intent: Intent) {
        val url = intent.dataString ?: return
        val sourceAppPkg = resolveSourcePackage(intent)

        try {
            shortXManager.onBrowserIntercept(url, sourceAppPkg)
        } catch (_: Exception) {
        }
    }

    private fun resolveSourcePackage(intent: Intent): String {
        val referrer = referrer
            ?: intent.getParcelableExtra<Uri>(Intent.EXTRA_REFERRER)
            ?: intent.getStringExtra(Intent.EXTRA_REFERRER_NAME)?.let(Uri::parse)

        return when (referrer?.scheme) {
            "android-app" -> referrer.host.orEmpty()
            else -> callingPackage.orEmpty()
        }
    }
}
