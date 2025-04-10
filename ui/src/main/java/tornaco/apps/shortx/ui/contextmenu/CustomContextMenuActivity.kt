package tornaco.apps.shortx.ui.contextmenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger

abstract class CustomContextMenuActivity : Activity() {
    private val logger = Logger(javaClass.simpleName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.action == Intent.ACTION_PROCESS_TEXT) {
            val selectedText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""
            logger.logOnDebug("selectedText: $selectedText")
            finish()
        }
    }
}

class CustomContextMenuActivity1 : CustomContextMenuActivity()
class CustomContextMenuActivity2 : CustomContextMenuActivity()
class CustomContextMenuActivity3 : CustomContextMenuActivity()
class CustomContextMenuActivity4 : CustomContextMenuActivity()
class CustomContextMenuActivity5 : CustomContextMenuActivity()
class CustomContextMenuActivity6 : CustomContextMenuActivity()