@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.demo

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.rosemoe.sora.app.CEActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import shortx.plugin.ParamsData
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.proto.action.PluginAction
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.settings.TileSettingsActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.util.Locale
import java.util.UUID

class DemoActivity : BaseActivity() {
    @Composable
    override fun Content() {
        ShortXTheme {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
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
                                    ShortXManagerNative.shortX?.log("Test log: ${System.currentTimeMillis()}")
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
                            val evaluateContext = EvaluateContext.newBuilder()
                                .setSrcId("Test-" + UUID.randomUUID().toString())
                                .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                                .setSrcTitle("Testing")
                                .build()
                            ShortXManagerNative.shortX?.executeAction(
                                ByteArrayWrapper(evaluateContext.toByteArray()),
                                ByteArrayWrapper(
                                    PluginAction.newBuilder()
                                        .setPluginId("shortx.pluginsdk")
                                        .setParams(
                                            ParamsData.newBuilder().putStringData("input", "Nick")
                                                .build()
                                        )
                                        .build().pack_().toByteArray()
                                )
                            )
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
    }
}