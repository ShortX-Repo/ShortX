@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.demo

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.rosemoe.sora.app.CEActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.INFCTagEndpointListener
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.settings.TileSettingsActivity
import java.util.Locale

class DemoActivity : BaseActivity() {
    private val nfcListener = object : INFCTagEndpointListener.Stub() {
        override fun onDispatchTagEndpoint(uid: ByteArray) {
            runOnUiThread {
                Toast.makeText(this@DemoActivity, "NFC: ${uid.joinToString()}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTaskDescription(ActivityManager.TaskDescription("Task description"))
    }

    override fun onResume() {
        super.onResume()
        shortXManager.registerNFCTagEndpointListener(nfcListener)
    }

    override fun onStop() {
        super.onStop()
        shortXManager.unregisterNFCTagEndpointListener(nfcListener)
    }

    @Composable
    override fun Content() {
        Scaffold(modifier = Modifier,
            topBar = {
                ShortXTopAppBar(
                    title = {
                        Text(
                            text = "Demos"
                        )
                    }
                )
            }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                val all = packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_CREATE_SHORTCUT),
                    131072
                )
                all.forEach {
                    composeLogger.d("$it")
                }


                NotificationDemo()
                MapDemo()
                VibrateDemo()

                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                Button(onClick = {
                    scope.launch {
                        while (true) {
                            ShortXManagerNative.shortX?.log(
                                "Test log: ${System.currentTimeMillis()}",
                                ""
                            )
                            delay(20)
                        }
                    }
                }) {
                    Text(text = "A lot of log")
                }


                FilledTonalButton(onClick = {
                    startActivity(Intent(this@DemoActivity, CEActivity::class.java))
                }) {
                    Text(text = "Crash")
                }

                FilledTonalButton(onClick = {
                    ShortXManagerNative.shortX?.crashSystemServer()
                }) {
                    Text(text = "Crash System")
                }

                FilledTonalButton(onClick = {
                    ShortXManagerNative.shortX?.reportFATALError("Test")
                }) {
                    Text(text = "Test fatal error")
                }

                FilledTonalButton(onClick = {
                }) {
                    Text(text = "Plugin action")
                }

                FilledTonalButton(onClick = {
                    AlertDialog.Builder(context)
                        .setAdapter(
                            ArrayAdapter(
                                context,
                                android.R.layout.simple_list_item_1,
                                android.R.id.text1,
                                listOf("A", "B", "C")
                            )
                        ) { dialog, which ->

                        }.create().show()
                }) {
                    Text(text = "Array dialog")
                }


                Button(onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.bing.com/?toWww=1&redig=9722F55035FE421E9F77031E6B4E3209")
                    )
                    Logger().d("startActivity: $intent")
                    startActivity(intent)
                }) {
                    Text(text = "Bing.com")
                }

                Button(onClick = {
                    TileSettingsActivity.start(context, false)
                }) {
                    Text(text = "Tile")
                }

                Button(onClick = {
                    context.startActivity(Intent(context, ComposeDemoActivity::class.java))
                }) {
                    Text(text = "Compose demo")
                }

                Button(onClick = {
                    context.startActivity(Intent(context, PickRingtoneActivity::class.java))
                }) {
                    Text(text = "Pick ringtone demo")
                }

                Button(onClick = {
                    context.startActivity(Intent(context, PieDemoActivity::class.java))
                }) {
                    Text(text = "Pie demo")
                }

                Button(onClick = {
                    context.startActivity(Intent(context, DialogStyleActivity::class.java))
                }) {
                    Text(text = "Dialog Activity")
                }

                Button(onClick = {
                    var tts: TextToSpeech? = null
                    tts = TextToSpeech(
                        context
                    ) { status ->
                        Logger().d("status: $status")
                        if (status == TextToSpeech.SUCCESS) {
                            val result = tts!!.setLanguage(Locale.getDefault())
                            if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED
                            ) {
                                Toast.makeText(
                                    context,
                                    "数据丢失或不支持",
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                            // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                            tts!!.setPitch(1.0f)
                            tts!!.speak(
                                "Hello 你好啊",
                                TextToSpeech.QUEUE_FLUSH, null
                            );
                        }
                    }
                }) {
                    Text(text = "TTS")
                }
            }
        }
    }
}
