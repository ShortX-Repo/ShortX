package tornaco.apps.shortx.ui.gesture

import android.content.Context
import android.content.Intent
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
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.time.Duration

@AndroidEntryPoint
class ScreenRectPickerActivity : BaseActivity() {
    private val logger = Logger("ScreenRectPickerActivity")

    companion object {
        @JvmStatic
        fun start(context: Context): Intent {
            return Intent(context, ScreenRectPickerActivity::class.java)
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
            val rectOrNull = byteRes?.let { Rect.parseFrom(it.byteData) }
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
                        text = LocalI18N.current["ui.screen.rect.picker.tips"]
                    )
                }
            }
        }
    }
}