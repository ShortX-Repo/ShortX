package tornaco.apps.shortx.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.REPEAT_MODE_OFF
import com.google.android.exoplayer2.ui.StyledPlayerView
import tornaco.apps.shortx.ui.base.BaseActivity

class EggActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, EggActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val context = LocalContext.current
                val uri =
                    Uri.parse("https://kstatic.googleusercontent.com/files/4300bb8849c8af5e871c2cdbadeca7794cf46f248011f4aa1056ae0c56e9af49178219ca04ba775714b63bf9e790e20bd70a9001b1c60c76570dccb703a864e0")
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .build()
                val exoPlayer = remember(context, mediaItem) {
                    ExoPlayer.Builder(context)
                        .build()
                        .also { exoPlayer ->
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.prepare()
                            exoPlayer.playWhenReady = false
                            exoPlayer.repeatMode = REPEAT_MODE_OFF
                        }
                }

                DisposableEffect(
                    AndroidView(factory = {
                        StyledPlayerView(context).apply {
                            player = exoPlayer
                        }
                    })
                ) {
                    onDispose { exoPlayer.release() }
                }
            }
        }
    }
}