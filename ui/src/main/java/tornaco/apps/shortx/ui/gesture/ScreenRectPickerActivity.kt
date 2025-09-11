package tornaco.apps.shortx.ui.gesture

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.time.Duration
import tornaco.apps.shortx.core.proto.common.Rect as ProtoRect

@AndroidEntryPoint
class ScreenRectPickerActivity : BaseActivity() {
    private val logger = Logger("ScreenRectPickerActivity")

    companion object {
        @JvmStatic
        fun start(context: Context): Intent {
            return Intent(context, ScreenRectPickerActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置窗口标志来更彻底地屏蔽系统手势
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        // 设置窗口属性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    @Composable
    override fun Content() {
        LaunchedEffect(Unit) {
            val receiver = SynchronousResultReceiver.get()
            shortXManager.showScreenAreaSelector(receiver)
            val receiverRes = receiver.awaitResultNoInterrupt(Duration.ofMinutes(60))
            logger.d("receiver get res: $receiverRes")
            val byteRes: ByteArrayWrapper? = receiverRes.getValue(null)
            val rectOrNull = byteRes?.let { ProtoRect.parseFrom(it.byteData) }
            logger.d("rectOrNull: $rectOrNull")
            setResult(RESULT_OK, Intent().apply {
                putExtra("rect", rectOrNull)
            })
            finish()
        }

        ShortXTheme {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SmallTitle(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = LocalI18N.current["ui.action.select.screen.area.tip"]
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 恢复系统UI
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}