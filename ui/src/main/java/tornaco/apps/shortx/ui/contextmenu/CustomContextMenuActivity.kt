package tornaco.apps.shortx.ui.contextmenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.CustomContextMenu
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger

abstract class CustomContextMenuActivity : Activity() {
    private val logger = Logger(javaClass.simpleName)

    abstract val menu: CustomContextMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.action == Intent.ACTION_PROCESS_TEXT) {
            val selectedText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""
            logger.logOnDebug("selectedText: $selectedText")
            shortXManager.onMenuActionTrigger(menu.number, selectedText)
            finish()
        }
    }
}

class CustomContextMenuActivity1 : CustomContextMenuActivity() {
    override val menu: CustomContextMenu
        get() = CustomContextMenu.CustomContextMenu1
}

class CustomContextMenuActivity2 : CustomContextMenuActivity() {
    override val menu: CustomContextMenu
        get() = CustomContextMenu.CustomContextMenu2
}

class CustomContextMenuActivity3 : CustomContextMenuActivity() {
    override val menu: CustomContextMenu
        get() = CustomContextMenu.CustomContextMenu3
}

class CustomContextMenuActivity4 : CustomContextMenuActivity() {
    override val menu: CustomContextMenu
        get() = CustomContextMenu.CustomContextMenu4
}