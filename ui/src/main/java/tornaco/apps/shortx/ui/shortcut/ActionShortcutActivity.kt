package tornaco.apps.shortx.ui.shortcut

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.IconCompat
import com.elvishew.xlog.XLog
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.res.remixResId
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.shortcutManager
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.base.i18NIns
import tornaco.apps.shortx.ui.main.model.DirectActionUM

private const val KEY_ACTION_ID = "key_action_id"

// For Deep link
private const val KEY_ID = "id"

class ActionShortcutActivity : Activity() {

    private val logger = Logger("ActionShortcutActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var id: String? = intent.getStringExtra(KEY_ACTION_ID)

        if (id == null) {
            val data: Uri? = intent.data
            logger.d("data: $data")
            if (data != null) {
                id = data.getQueryParameter(KEY_ID)
            }
        }

        logger.i("ActionShortcutActivity, action id: $id")

        if (id == null) {
            Toast.makeText(this, "Id is null", Toast.LENGTH_SHORT).show()
            finishAffinity()
            return
        }

        val da = shortXManager.getDirectActionById(id)
        if (da == null) {
            Toast.makeText(this, i18NIns["ui.action.from.da.not.found"], Toast.LENGTH_SHORT).show()
            finishAffinity()
            return
        }

        val evaluateContext = EvaluateContext.newBuilder()
            .setSrcId(da.id)
            .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteRuleActions)
            .setSrcTitle(da.title)
            .build()
        shortXManager.executeDirectionActionById(evaluateContext, da.id)

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

                ShortcutManagerCompat.requestPinShortcut(
                    context,
                    createShortcutInfo(context, action),
                    ShortcutReceiver.getPinRequestAcceptedIntent(context).intentSender
                )
            }
        }

        fun addDynamicShortcut(context: Context, action: DirectActionUM): Boolean {
            val shortcutInfoIntent = createIntent(context, action.id)
            shortcutInfoIntent.action = Intent.ACTION_VIEW

            val resource = createIconForDA(context, action)
            val icon = resource?.let { IconCompat.createWithBitmap(it) }
                ?: IconCompat.createWithResource(
                    context,
                    github.tornaco.shortx.icons.R.drawable.ic_remix_android_fill
                )

            val shortcutInfo = ShortcutInfoCompat.Builder(context, action.id)
                .setShortLabel(action.title)
                .setLongLabel(action.title)
                .setIcon(icon)
                .setIntent(shortcutInfoIntent)
                .build()

            return ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo)
        }

        fun removeDynamicShortcut(context: Context, action: DirectActionUM) {
            removeDynamicShortcut(context, action.id)
        }

        fun removeDynamicShortcut(context: Context, id: String) {
            ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(id))
        }

        fun hasDynamicShortcut(context: Context, action: DirectActionUM): Boolean {
            val shortcutManager = context.shortcutManager
            return shortcutManager.dynamicShortcuts.any {
                it.id == action.id
            }
        }

        fun createShortcutInfo(context: Context, action: DirectActionUM): ShortcutInfoCompat {

            val shortcutInfoIntent = createIntent(context, action.id)
            shortcutInfoIntent.action = Intent.ACTION_VIEW

            val resource = createIconForDA(context, action)

            return ShortcutInfoCompat.Builder(context, "ShortX-Action-" + action.id)
                .setIcon(resource?.let { IconCompat.createWithBitmap(it) })
                .setShortLabel(action.title)
                .setIntent(shortcutInfoIntent)
                .build()
        }

        private fun createIconForDA(context: Context, action: DirectActionUM): Bitmap? {
            val actionIconDrawable = ResourcesCompat.getDrawable(
                context.resources,
                context.remixResId(action.icon),
                null
            )?.apply {
                setTint(android.graphics.Color.WHITE)
            }
            val templateDrawable =
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ui_ic_shortcut_template,
                    null
                )
            val ld = templateDrawable as LayerDrawable
            ld.setTint(Color(action.iconColor).toArgb())
            ld.setDrawableByLayerId(R.id.settings_ic_foreground, actionIconDrawable)

            return BitmapUtils.getBitmap(context, templateDrawable)
        }
    }

}