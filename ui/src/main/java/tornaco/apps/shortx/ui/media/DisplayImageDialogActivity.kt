package tornaco.apps.shortx.ui.media

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivityStarter.EXTRA_REQUEST_CODE
import tornaco.apps.shortx.ui.media.DisplayImageActivityStarter.EXTRA_IMAGE_URL
import tornaco.apps.shortx.ui.util.GlideApp

class DisplayImageDialogActivity : AppCompatActivity() {
    private val logger = Logger("DisplayImageActivity")

    private var requestCode = 0
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resolveIntent()
        showAlertDialog()
    }

    private fun showAlertDialog() {
        logger.i("Display: $imageUrl")
        val root = LayoutInflater.from(this).inflate(
            R.layout.ui_display_image,
            null,
            false
        ).apply {
            val imageView = findViewById<ImageView>(R.id.image)
            GlideApp.with(this@DisplayImageDialogActivity)
                .load(imageUrl)
                .into(imageView)
        }
        MaterialAlertDialogBuilder(this)
            .setView(root)
            .setTitle(null)
            .setOnDismissListener {
                onBackPressed()
            }
            .create().apply {
                show()
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