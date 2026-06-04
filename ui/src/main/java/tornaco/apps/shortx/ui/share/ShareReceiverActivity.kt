package tornaco.apps.shortx.ui.share

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import tornaco.apps.shortx.core.shortXManager


class ShareReceiverActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        if (intent?.action == Intent.ACTION_SEND) {
            handleSendIntent(intent)
        }
        finish()
    }

    private fun handleSendIntent(intent: Intent) {
        val mimeType = intent.type ?: "*/*"
        val sourcePkg = intent.`package` ?: ""

        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        val sharedUri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)?.toString() ?: ""

        if (sharedText.isBlank() && sharedUri.isBlank()) {
            Toast.makeText(this, "No content to share", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            shortXManager.onIncomingShare(
                sharedText = sharedText,
                sharedUri = sharedUri,
                mimeType = mimeType,
                sourceAppPkg = sourcePkg
            )
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
