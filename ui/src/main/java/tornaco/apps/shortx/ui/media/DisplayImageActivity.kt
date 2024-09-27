package tornaco.apps.shortx.ui.media

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ortiz.touchview.TouchImageView
import tornaco.apps.shortx.core.proto.common.ImageScaleType
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.biometric.BiometricVerifyActivityStarter.EXTRA_REQUEST_CODE
import tornaco.apps.shortx.ui.media.DisplayImageActivityStarter.EXTRA_IMAGE_SCALE_TYPE
import tornaco.apps.shortx.ui.media.DisplayImageActivityStarter.EXTRA_IMAGE_URL
import tornaco.apps.shortx.ui.util.GlideApp

class DisplayImageActivity : AppCompatActivity() {
    private val logger = Logger("DisplayImageActivity")

    private var requestCode = 0
    private var imageUrl: String = ""
    private var scaleType: ImageScaleType = ImageScaleType.IMAGE_SCALE_TYPE_FIT_CENTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resolveIntent()
        enableEdgeToEdge()
        setContentView(R.layout.ui_display_image)
        val imageView = findViewById<TouchImageView>(R.id.image).apply {
            scaleType = this@DisplayImageActivity.scaleType.androidType
            maxZoom = 6f
        }
        val loadingView = findViewById<TextView>(R.id.loading)
        val errorView = findViewById<TextView>(R.id.error)

        GlideApp.with(this)
            .load(imageUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(object : CustomTarget<Drawable?>() {
                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                    loadingView.isVisible = true
                    errorView.isVisible = false
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    loadingView.isVisible = false
                    errorView.isVisible = true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    imageView.setImageDrawable(resource)
                    loadingView.isVisible = false
                    errorView.isVisible = false
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit

            })
    }

    private fun resolveIntent() {
        requestCode =
            intent.getIntExtra(EXTRA_REQUEST_CODE, Int.MIN_VALUE)
        require(requestCode >= 0) {
            "Bad requestCode: $requestCode"
        }
        imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL).fallbackOnEmpty("")
        scaleType = ImageScaleType.valueOf(
            intent.getStringExtra(EXTRA_IMAGE_SCALE_TYPE)
                .fallbackOnEmpty(ImageScaleType.IMAGE_SCALE_TYPE_FIT_CENTER.name)
        )
    }

    override fun onStop() {
        super.onStop()
        onBackPressed()
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

    private val ImageScaleType.androidType
        get() = when (this) {
            ImageScaleType.IMAGE_SCALE_TYPE_FIT_CENTER -> {
                ImageView.ScaleType.FIT_CENTER
            }

            ImageScaleType.IMAGE_SCALE_TYPE_FIT_XY -> {
                ImageView.ScaleType.FIT_XY
            }

            ImageScaleType.IMAGE_SCALE_TYPE_CENTER -> {
                ImageView.ScaleType.CENTER
            }

            ImageScaleType.IMAGE_SCALE_TYPE_CENTER_CROP -> {
                ImageView.ScaleType.CENTER_CROP
            }

            ImageScaleType.IMAGE_SCALE_TYPE_CENTER_INSIDE -> {
                ImageView.ScaleType.CENTER_INSIDE
            }

            ImageScaleType.IMAGE_SCALE_TYPE_MATRIX -> {
                ImageView.ScaleType.MATRIX
            }

            ImageScaleType.UNRECOGNIZED -> {
                ImageView.ScaleType.FIT_CENTER
            }
        }
}