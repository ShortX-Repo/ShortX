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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.settings.TileSettingsActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.util.Locale

class DemoActivity : BaseActivity() {
    @Composable
    override fun Content() {
        ShortXTheme {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        ShortXMediumTopAppBar(
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
                            .padding(16.dp),
                    ) {
                        NotificationDemo()
                        MapDemo()
                        VibrateDemo()

                        FilledTonalButton(onClick = {
                            throw IllegalArgumentException()
                        }) {
                            Text(text = "Crash")
                        }

                        FilledTonalButton(onClick = {
                            ShortXManagerNative.shortX?.crashSystemServer()
                        }) {
                            Text(text = "Crash System")
                        }

                        val context = LocalContext.current
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