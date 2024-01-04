package tornaco.apps.shortx.ui.main

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger

class DeepLinkTriggerActivity : Activity() {

    companion object {
        // shortx://trigger?tag=""
        const val TAG = "tag"
    }

    private val logger = Logger("DeepLinkTriggerActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data: Uri? = intent.data
        logger.d("data: $data")
        var tag: String? = null
        if (data != null) {
            tag = data.getQueryParameter(TAG)
        }
        logger.d("tag = $tag")

        if (tag == null) {
            Toast.makeText(this, "Tag is null", Toast.LENGTH_SHORT).show()
            finishAffinity()
            return
        }

        shortXManager.deepLinkTriggerCalled(tag)
        finishAffinity()
    }

}