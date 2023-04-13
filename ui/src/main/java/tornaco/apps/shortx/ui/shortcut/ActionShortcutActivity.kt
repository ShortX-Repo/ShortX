package tornaco.apps.shortx.ui.shortcut

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.IconCompat
import com.elvishew.xlog.XLog
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.main.DirectActionUM

private const val KEY_ACTION_ID = "key_action_id"

class ActionShortcutActivity : Activity() {

    private val logger = Logger("ActionShortcutActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id: String? = intent.getStringExtra(KEY_ACTION_ID)
        logger.i("ActionShortcutActivity, action: $id")
        finishAffinity()
    }

    companion object {
        private fun createIntent(context: Context?, actionId: String): Intent {
            XLog.d("ActionShortcutActivity, createIntent: $actionId")
            val intent = Intent(context, ActionShortcutActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(KEY_ACTION_ID, actionId)
            return intent
        }

        fun addShortcut(context: Context, action: DirectActionUM) {
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
                val drawable = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ui_ic_baseline_developer_mode_24,
                    null
                )
                val resource: Bitmap? = BitmapUtils.getBitmap(context, drawable)
                val shortcutInfoIntent = createIntent(context, action.id)
                shortcutInfoIntent.action = Intent.ACTION_VIEW
                val info = ShortcutInfoCompat.Builder(context, "ShortX-Action-" + action.id)
                    .setIcon(resource?.let { IconCompat.createWithBitmap(it) })
                    .setShortLabel(action.title)
                    .setIntent(shortcutInfoIntent)
                    .build()
                ShortcutManagerCompat.requestPinShortcut(
                    context,
                    info,
                    ShortcutReceiver.getPinRequestAcceptedIntent(context).getIntentSender()
                )
            }
        }
    }

}