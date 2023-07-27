package tornaco.apps.shortx.ui.gesture

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.protobuf.ByteString
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.ParcelableUtil
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.i18NIns
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class GestureRecorderActivity : BaseActivity() {
    private val logger = Logger("ShortXGestureRecorder")
    private val events = mutableListOf<ByteArray>()

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, GestureRecorderActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        ShortXTheme {
            var startRecord by remember {
                mutableStateOf(false)
            }
            if (!startRecord) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SmallTitle(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = LocalI18N.current["ui.gesture.record.tips"]
                        )
                        StandardSpacer()
                        Button(onClick = {
                            startRecord = true
                        }) {
                            Text(text = LocalI18N.current["ui.gesture.record.start"])
                        }
                    }
                }
            }

            if (startRecord) {
                DrawingApp {
                    logger.d("onEvent: $it")
                    events.add(ParcelableUtil.marshall(it))
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            val eventRecord = GestureRecord.newBuilder()
                .setLabel(DateUtils.formatForFileName(System.currentTimeMillis()))
                .setId("test")
                .addAllEventData(
                    events.map {
                        ByteString.copyFrom(it)
                    }
                )
                .build()

            logger.d("eventRecord: $eventRecord")
            shortXManager.addGestureRecord(eventRecord)
            Toast.makeText(this, i18NIns["ui.common.saved"], Toast.LENGTH_SHORT).show()

            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}