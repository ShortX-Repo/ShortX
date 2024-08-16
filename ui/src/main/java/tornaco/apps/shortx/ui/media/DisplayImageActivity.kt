package tornaco.apps.shortx.ui.media

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import com.skydoves.landscapist.glide.GlideImage
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivityStarter.EXTRA_REQUEST_CODE
import tornaco.apps.shortx.ui.media.DisplayImageActivityStarter.EXTRA_IMAGE_URL

class DisplayImageActivity : BaseActivity() {
    private val logger = Logger("DisplayImageActivity")

    private var requestCode = 0
    private var imageUrl: String = ""

    override fun beforeOnCreate() {
        super.beforeOnCreate()
        resolveIntent()
    }

    @Composable
    override fun Content() {
        Surface {
            GlideImage(
                imageModel = { imageUrl },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    private fun resolveIntent() {
        requestCode =
            intent.getIntExtra(EXTRA_REQUEST_CODE, Int.MIN_VALUE)
        require(requestCode >= 0) {
            "Bad requestCode: $requestCode"
        }
        imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL).fallbackOnEmpty("")
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        shortXManager.setStartActivityResult(
            requestCode,
            RESULT_CANCELED,
            bundleOf()
        )
        cancelVerifyAndFinish()
    }

    private fun cancelVerifyAndFinish() {
        logger.w("cancelVerifyAndFinish...")
        setResult(RESULT_CANCELED)
        finish()
    }
}